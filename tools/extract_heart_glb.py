"""Extract heart2.glb → heart_mesh.bin + heart.png (optimized, smooth UVs/normals)."""
from __future__ import annotations

import struct
from io import BytesIO
from pathlib import Path

import numpy as np
from PIL import Image
from pygltflib import GLTF2

SRC = Path(r"c:\Users\Lev\Desktop\heart2.glb")
OUT_DIR = Path(r"D:\AAC\aware-and-conscious.jar-main\src\main\resources\assets\outofbound")
OUT_BIN = OUT_DIR / "models" / "entity" / "heart_mesh.bin"
OUT_OBJ = OUT_DIR / "models" / "entity" / "heart.obj"
OUT_TEX = OUT_DIR / "textures" / "entities" / "heart.png"

SUBDIV_LEVELS = 2
TARGET_HEIGHT = 1.45

def read_accessor(g: GLTF2, blob: bytes, index: int) -> np.ndarray:
	a = g.accessors[index]
	bv = g.bufferViews[a.bufferView]
	off = (bv.byteOffset or 0) + (a.byteOffset or 0)
	comps = {"SCALAR": 1, "VEC2": 2, "VEC3": 3, "VEC4": 4}[a.type]
	ctype = a.componentType
	fmt = {5126: "f", 5123: "H", 5125: "I", 5121: "B"}[ctype]
	size = {5126: 4, 5123: 2, 5125: 4, 5121: 1}[ctype]
	stride = bv.byteStride or (size * comps)
	rows = []
	for k in range(a.count):
		o = off + k * stride
		rows.append(struct.unpack_from("<" + fmt * comps, blob, o))
	dtype = np.float64 if ctype == 5126 else np.int64
	return np.asarray(rows, dtype=dtype)

def subdivide(pos: np.ndarray, uv: np.ndarray, idx: np.ndarray) -> tuple[np.ndarray, np.ndarray, np.ndarray]:
	"""One mid-point subdivision pass; interpolates pos/uv, clamps UV."""
	cache: dict[tuple[int, int], int] = {}
	new_pos = pos.tolist()
	new_uv = uv.tolist()

	def midpoint(i: int, j: int) -> int:
		key = (i, j) if i < j else (j, i)
		if key in cache:
			return cache[key]
		p = 0.5 * (pos[i] + pos[j])
		t = 0.5 * (uv[i] + uv[j])
		t = np.clip(t, 0.0, 1.0)
		ni = len(new_pos)
		new_pos.append(p.tolist())
		new_uv.append(t.tolist())
		cache[key] = ni
		return ni

	new_idx = []
	for a, b, c in idx.reshape(-1, 3):
		a, b, c = int(a), int(b), int(c)
		ab = midpoint(a, b)
		bc = midpoint(b, c)
		ca = midpoint(c, a)
		new_idx.extend((a, ab, ca, ab, b, bc, ca, bc, c, ab, bc, ca))
	return np.asarray(new_pos, dtype=np.float64), np.asarray(new_uv, dtype=np.float64), np.asarray(new_idx, dtype=np.int32)

def smooth_normals(pos: np.ndarray, idx: np.ndarray) -> np.ndarray:
	nrm = np.zeros_like(pos)
	tris = idx.reshape(-1, 3)
	for a, b, c in tris:
		p0, p1, p2 = pos[a], pos[b], pos[c]
		fn = np.cross(p1 - p0, p2 - p0)
		ln = np.linalg.norm(fn)
		if ln > 1e-10:
			fn /= ln
		nrm[a] += fn
		nrm[b] += fn
		nrm[c] += fn
	lens = np.linalg.norm(nrm, axis=1)
	lens[lens < 1e-10] = 1.0
	return nrm / lens[:, None]

def main() -> None:
	g = GLTF2().load(SRC)
	blob = g.binary_blob()
	prim = g.meshes[0].primitives[0]
	pos = read_accessor(g, blob, prim.attributes.POSITION)
	uv = read_accessor(g, blob, prim.attributes.TEXCOORD_0)
	idx = read_accessor(g, blob, prim.indices).astype(np.int32).reshape(-1)

	mins = pos.min(axis=0)
	maxs = pos.max(axis=0)
	center = np.array([(mins[0] + maxs[0]) * 0.5, mins[1], (mins[2] + maxs[2]) * 0.5])
	extent = float((maxs - mins).max()) or 1.0
	scale = TARGET_HEIGHT / extent
	pos = (pos - center) * scale

	uv = np.clip(uv, 0.0, 1.0)

	for _ in range(SUBDIV_LEVELS):
		pos, uv, idx = subdivide(pos, uv, idx)

	nrm = smooth_normals(pos, idx)

	baked = []
	for a, b, c in idx.reshape(-1, 3):
		for i in (a, b, c):
			baked.extend(
				(
					float(pos[i, 0]),
					float(pos[i, 1]),
					float(pos[i, 2]),
					float(nrm[i, 0]),
					float(nrm[i, 1]),
					float(nrm[i, 2]),
					float(uv[i, 0]),
					float(uv[i, 1]),
				)
			)
	vert_count = len(baked) // 8
	print(f"baked verts={vert_count} tris={vert_count // 3}")

	OUT_BIN.parent.mkdir(parents=True, exist_ok=True)
	with OUT_BIN.open("wb") as f:
		f.write(struct.pack("<I", 0x31545248))
		f.write(struct.pack("<i", vert_count))
		for v in baked:
			f.write(struct.pack("<f", v))

	with OUT_OBJ.open("w", encoding="utf-8") as f:
		f.write("# heart2.glb optimized\n")
		for p in pos:
			f.write(f"v {p[0]:.6f} {p[1]:.6f} {p[2]:.6f}\n")
		for t in uv:
			f.write(f"vt {t[0]:.6f} {t[1]:.6f}\n")
		for a, b, c in idx.reshape(-1, 3):
			f.write(f"f {a+1}/{a+1} {b+1}/{b+1} {c+1}/{c+1}\n")

	im = g.images[0]
	bv = g.bufferViews[im.bufferView]
	off = bv.byteOffset or 0
	raw = blob[off : off + bv.byteLength]
	img = Image.open(BytesIO(raw)).convert("RGBA")
	img = img.resize((256, 256), Image.Resampling.NEAREST)
	OUT_TEX.parent.mkdir(parents=True, exist_ok=True)
	img.save(OUT_TEX, optimize=True)
	print("wrote", OUT_BIN, OUT_BIN.stat().st_size, "bytes;", OUT_TEX, OUT_TEX.stat().st_size, "bytes")

if __name__ == "__main__":
	main()
