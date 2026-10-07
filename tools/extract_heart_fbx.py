"""Extract Vertices + UVs + indices from Kaydara FBX Binary → heart_mesh.bin / OBJ."""
from __future__ import annotations
import struct
import zlib
from pathlib import Path

SRC = Path(r"c:\Users\Lev\Downloads\Heart.fbx")
OUT_DIR = Path(r"D:\AAC\aware-and-conscious.jar-main\src\main\resources\assets\outofbound\models\entity")
OUT_OBJ = OUT_DIR / "heart.obj"
OUT_BIN = OUT_DIR / "heart_mesh.bin"

def read_array(data: bytes, offset: int):
	t = chr(data[offset])
	offset += 1
	length, encoding, clen = struct.unpack_from("<III", data, offset)
	offset += 12
	blob = data[offset : offset + clen]
	offset += clen
	if encoding == 1:
		blob = zlib.decompress(blob)
	elif encoding != 0:
		raise ValueError(f"unknown encoding {encoding}")
	if t == "d":
		vals = list(struct.unpack_from(f"<{length}d", blob))
	elif t == "f":
		vals = list(struct.unpack_from(f"<{length}f", blob))
	elif t == "i":
		vals = list(struct.unpack_from(f"<{length}i", blob))
	else:
		raise ValueError(f"unsupported array type {t}")
	return vals, offset

def find_property_array(data: bytes, name: bytes):
	idx = data.find(name)
	if idx < 0:
		raise RuntimeError(f"{name!r} not found")
	start = idx + len(name)
	for i in range(start, min(len(data) - 16, start + 4096)):
		if data[i] in (ord("d"), ord("f"), ord("i")):
			try:
				vals, _ = read_array(data, i)
				if len(vals) >= 3:
					return vals
			except Exception:
				continue
	raise RuntimeError(f"array for {name!r} not found")

def find_uv_array(data: bytes):
	"""LayerElementUV → UV doubles (ByPolygonVertex / IndexToDirect)."""
	start = data.find(b"LayerElementUV")
	if start < 0:
		raise RuntimeError("LayerElementUV missing")
	for i in range(start, min(len(data) - 16, start + 300_000)):
		if data[i] != ord("d"):
			continue
		try:
			vals, _ = read_array(data, i)
			if len(vals) >= 6 and len(vals) % 2 == 0:
				return vals
		except Exception:
			continue
	raise RuntimeError("UV array not found")

def main():
	data = SRC.read_bytes()
	verts_flat = find_property_array(data, b"Vertices")
	indices = find_property_array(data, b"PolygonVertexIndex")
	uvs_flat = find_uv_array(data)
	uv_indices = find_property_array(data, b"UVIndex")
	print("vertex floats", len(verts_flat), "indices", len(indices),
		  "uv floats", len(uvs_flat), "uvIndex", len(uv_indices))

	verts = [(verts_flat[i], verts_flat[i + 1], verts_flat[i + 2]) for i in range(0, len(verts_flat), 3)]
	uvs = [(uvs_flat[i], uvs_flat[i + 1]) for i in range(0, len(uvs_flat), 2)]

	xs = [v[0] for v in verts]
	ys = [v[1] for v in verts]
	zs = [v[2] for v in verts]
	minx, maxx = min(xs), max(xs)
	miny, maxy = min(ys), max(ys)
	minz, maxz = min(zs), max(zs)
	cx = (minx + maxx) * 0.5
	cy = miny
	cz = (minz + maxz) * 0.5
	extent = max(maxx - minx, maxy - miny, maxz - minz) or 1.0
	scale = 1.35 / extent
	verts = [((x - cx) * scale, (y - cy) * scale, (z - cz) * scale) for x, y, z in verts]

	faces: list[tuple[tuple[int, int], tuple[int, int], tuple[int, int]]] = []
	poly: list[tuple[int, int]] = []
	corner_i = 0
	for raw in indices:
		uv_i = int(uv_indices[corner_i])
		corner_i += 1
		if raw < 0:
			poly.append(((-raw) - 1, uv_i))
			if len(poly) >= 3:
				for i in range(1, len(poly) - 1):
					faces.append((poly[0], poly[i], poly[i + 1]))
			poly = []
		else:
			poly.append((int(raw), uv_i))
	print("faces", len(faces), "uvs", len(uvs))

	OUT_DIR.mkdir(parents=True, exist_ok=True)
	with OUT_OBJ.open("w", encoding="utf-8") as f:
		f.write("# heart from Heart.fbx with UVs\n")
		for x, y, z in verts:
			f.write(f"v {x:.6f} {y:.6f} {z:.6f}\n")
		for u, v in uvs:
			f.write(f"vt {u:.6f} {v:.6f}\n")
		for (a, ua), (b, ub), (c, uc) in faces:
			f.write(f"f {a+1}/{ua+1} {b+1}/{ub+1} {c+1}/{uc+1}\n")

	with OUT_BIN.open("wb") as f:
		f.write(struct.pack("<i", len(verts)))
		for x, y, z in verts:
			f.write(struct.pack("<fff", x, y, z))
		f.write(struct.pack("<i", len(uvs)))
		for u, v in uvs:
			f.write(struct.pack("<ff", float(u), float(v)))
		f.write(struct.pack("<i", len(faces)))
		for (a, ua), (b, ub), (c, uc) in faces:
			f.write(struct.pack("<iiiiii", a, b, c, ua, ub, uc))
	print("wrote", OUT_OBJ, OUT_BIN, "size", OUT_BIN.stat().st_size)

if __name__ == "__main__":
	main()
