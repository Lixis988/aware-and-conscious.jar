package net.lixis.outofbound.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lixis.outofbound.OutofboundMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public final class HeartMesh {

	public static final ResourceLocation MESH_ID =
			new ResourceLocation(OutofboundMod.MODID, "models/entity/heart_mesh.bin");

	private static final int MAGIC_HRT1 = 0x31545248;

	private static float[] baked;
	private static boolean attempted;

	private HeartMesh() {
	}

	public static void ensureLoaded() {
		if (attempted) {
			return;
		}
		attempted = true;
		try {
			Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(MESH_ID);
			if (resource.isEmpty()) {
				OutofboundMod.LOGGER.error("Missing heart mesh {}", MESH_ID);
				return;
			}
			try (InputStream in = resource.get().open(); DataInputStream data = new DataInputStream(in)) {
				int magic = leInt(data);
				if (magic == MAGIC_HRT1) {
					loadHrt1(data);
				} else {

					loadLegacy(data, magic);
				}
			}
		} catch (Exception e) {
			OutofboundMod.LOGGER.error("Failed to load heart mesh", e);
			baked = null;
		}
	}

	private static void loadHrt1(DataInputStream data) throws Exception {
		int vertCount = leInt(data);
		if (vertCount <= 0 || vertCount > 200_000 || vertCount % 3 != 0) {
			OutofboundMod.LOGGER.error("Bad HRT1 vert count {}", vertCount);
			return;
		}
		float[] out = new float[vertCount * 8];
		for (int i = 0; i < out.length; i++) {
			out[i] = leFloat(data);
		}
		baked = out;
		OutofboundMod.LOGGER.info("Loaded heart mesh HRT1: {} verts ({} tris)", vertCount, vertCount / 3);
	}

	private static void loadLegacy(DataInputStream data, int vertCount) throws Exception {
		if (vertCount <= 0 || vertCount > 200_000) {
			OutofboundMod.LOGGER.error("Bad heart mesh vert count {}", vertCount);
			return;
		}
		float[] positions = new float[vertCount * 3];
		for (int i = 0; i < positions.length; i++) {
			positions[i] = leFloat(data);
		}
		int uvCount = leInt(data);
		if (uvCount <= 0 || uvCount > 200_000) {
			OutofboundMod.LOGGER.error("Bad heart mesh uv count {}", uvCount);
			return;
		}
		float[] uvs = new float[uvCount * 2];
		for (int i = 0; i < uvs.length; i++) {
			uvs[i] = leFloat(data);
		}
		int faceCount = leInt(data);
		if (faceCount <= 0 || faceCount > 400_000) {
			OutofboundMod.LOGGER.error("Bad heart mesh face count {}", faceCount);
			return;
		}
		float[] out = new float[faceCount * 3 * 8];
		int o = 0;
		for (int f = 0; f < faceCount; f++) {
			int ia = leInt(data);
			int ib = leInt(data);
			int ic = leInt(data);
			int ua = leInt(data);
			int ub = leInt(data);
			int uc = leInt(data);
			if (ia < 0 || ib < 0 || ic < 0 || ia >= vertCount || ib >= vertCount || ic >= vertCount
					|| ua < 0 || ub < 0 || uc < 0 || ua >= uvCount || ub >= uvCount || uc >= uvCount) {
				OutofboundMod.LOGGER.error("Heart mesh index OOB");
				return;
			}
			int pa = ia * 3, pb = ib * 3, pc = ic * 3;
			float ax = positions[pa], ay = positions[pa + 1], az = positions[pa + 2];
			float bx = positions[pb], by = positions[pb + 1], bz = positions[pb + 2];
			float cx = positions[pc], cy = positions[pc + 1], cz = positions[pc + 2];
			float ux = bx - ax, uy = by - ay, uz = bz - az;
			float vx = cx - ax, vy = cy - ay, vz = cz - az;
			float nx = uy * vz - uz * vy;
			float ny = uz * vx - ux * vz;
			float nz = ux * vy - uy * vx;
			float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
			if (len > 1.0e-5F) {
				nx /= len;
				ny /= len;
				nz /= len;
			}
			o = put(out, o, ax, ay, az, nx, ny, nz, uvs[ua * 2], 1.0F - uvs[ua * 2 + 1]);
			o = put(out, o, bx, by, bz, nx, ny, nz, uvs[ub * 2], 1.0F - uvs[ub * 2 + 1]);
			o = put(out, o, cx, cy, cz, nx, ny, nz, uvs[uc * 2], 1.0F - uvs[uc * 2 + 1]);
		}
		baked = out;
		OutofboundMod.LOGGER.info("Loaded legacy heart mesh: {} verts, {} uvs, {} tris", vertCount, uvCount, faceCount);
	}

	private static int leInt(DataInputStream data) throws Exception {
		return Integer.reverseBytes(data.readInt());
	}

	private static float leFloat(DataInputStream data) throws Exception {
		return Float.intBitsToFloat(Integer.reverseBytes(data.readInt()));
	}

	private static int put(float[] out, int o, float x, float y, float z, float nx, float ny, float nz, float u, float v) {
		out[o++] = x;
		out[o++] = y;
		out[o++] = z;
		out[o++] = nx;
		out[o++] = ny;
		out[o++] = nz;
		out[o++] = u;
		out[o++] = v;
		return o;
	}

	public static boolean isReady() {
		ensureLoaded();
		return baked != null;
	}

	public static void render(PoseStack poseStack, VertexConsumer consumer, int packedLight, int r, int g, int b, int a) {
		if (!isReady()) {
			return;
		}
		PoseStack.Pose pose = poseStack.last();
		Matrix4f matrix = pose.pose();
		Matrix3f normal = pose.normal();
		float[] mesh = baked;
		for (int i = 0; i < mesh.length; i += 8) {
			consumer.vertex(matrix, mesh[i], mesh[i + 1], mesh[i + 2])
					.color(r, g, b, a)
					.uv(mesh[i + 6], mesh[i + 7])
					.overlayCoords(OverlayTexture.NO_OVERLAY)
					.uv2(packedLight)
					.normal(normal, mesh[i + 3], mesh[i + 4], mesh[i + 5])
					.endVertex();
		}
	}
}
