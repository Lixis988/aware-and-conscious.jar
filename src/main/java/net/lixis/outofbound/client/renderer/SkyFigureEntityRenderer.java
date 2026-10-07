package net.lixis.outofbound.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.lixis.outofbound.entity.SkyFigureEntity;
import net.lixis.outofbound.world.SkyFigureType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SkyFigureEntityRenderer extends EntityRenderer<SkyFigureEntity> {

	private static final ResourceLocation DUMMY = new ResourceLocation("minecraft", "textures/misc/white.png");

	public SkyFigureEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.0F;
	}

	@Override
	public ResourceLocation getTextureLocation(SkyFigureEntity entity) {
		return DUMMY;
	}

	@Override
	public void render(SkyFigureEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight) {
		SkyFigureType type = SkyFigureType.values()[Mth.clamp(entity.getFigureTypeOrdinal(), 0, SkyFigureType.values().length - 1)];
		if (type.isBlockBased()) {
			return;
		}

		float size = entity.getFigureSize();
		int seed = entity.getVariantSeed();
		float age = entity.tickCount + partialTicks;
		List<LineSegment> segments = buildSegments(type, size, seed, age);

		poseStack.pushPose();
		poseStack.translate(0.0D, 0.0D, 0.0D);
		if (type == SkyFigureType.WIRE_CUBE) {
			poseStack.mulPose(Axis.YP.rotationDegrees(age * 2.5F));
			poseStack.mulPose(Axis.XP.rotationDegrees(age * 1.5F));
		}

		VertexConsumer consumer = buffer.getBuffer(RenderType.lines());
		Matrix4f matrix = poseStack.last().pose();
		for (LineSegment segment : segments) {
			drawLine(consumer, matrix, segment.from(), segment.to(), 0.45F, 0.05F, 0.05F, 1.0F);
		}
		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	private static List<LineSegment> buildSegments(SkyFigureType type, float size, int seed, float age) {
		return switch (type) {
			case KOCH_STAR -> kochStar(size);
			case SIERPINSKI_TETRAHEDRON -> sierpinskiTetrahedron(size, 2);
			case SPIRAL_TOWER -> spiralTower(size, seed);
			case WIRE_CUBE -> wireCube(size);
			default -> wireCube(size);
		};
	}

	private static List<LineSegment> wireCube(float size) {
		List<LineSegment> segments = new ArrayList<>();
		float half = size * 0.5F;
		Vector3f[] corners = {
				new Vector3f(-half, -half, -half),
				new Vector3f(half, -half, -half),
				new Vector3f(half, half, -half),
				new Vector3f(-half, half, -half),
				new Vector3f(-half, -half, half),
				new Vector3f(half, -half, half),
				new Vector3f(half, half, half),
				new Vector3f(-half, half, half)
		};
		int[][] edges = {
				{0, 1}, {1, 2}, {2, 3}, {3, 0},
				{4, 5}, {5, 6}, {6, 7}, {7, 4},
				{0, 4}, {1, 5}, {2, 6}, {3, 7}
		};
		for (int[] edge : edges) {
			segments.add(new LineSegment(corners[edge[0]], corners[edge[1]]));
		}
		return segments;
	}

	private static List<LineSegment> spiralTower(float size, int seed) {
		List<LineSegment> segments = new ArrayList<>();
		int steps = 24 + Math.floorMod(seed, 12);
		float radius = size * 0.35F;
		float height = size * 1.2F;
		Vector3f previous = null;
		for (int i = 0; i <= steps; i++) {
			float t = i / (float) steps;
			double angle = t * Math.PI * 4.0D + Math.floorMod(seed, 360);
			float x = (float) (Math.cos(angle) * radius);
			float z = (float) (Math.sin(angle) * radius);
			float y = -height * 0.5F + height * t;
			Vector3f current = new Vector3f(x, y, z);
			if (previous != null) {
				segments.add(new LineSegment(previous, current));
			}
			previous = current;
		}
		return segments;
	}

	private static List<LineSegment> kochStar(float size) {
		List<LineSegment> segments = new ArrayList<>();
		float radius = size * 0.55F;
		Vector3f[] points = new Vector3f[6];
		for (int i = 0; i < 6; i++) {
			double angle = (Math.PI * 2.0D * i) / 6.0D - Math.PI / 2.0D;
			float x = (float) (Math.cos(angle) * radius);
			float y = (float) (Math.sin(angle) * radius);
			points[i] = new Vector3f(x, y, 0.0F);
		}
		for (int i = 0; i < 6; i++) {
			kochSegment(segments, points[i], points[(i + 1) % 6], 2);
		}
		for (int i = 0; i < 3; i++) {
			Vector3f inner = points[i * 2].lerp(points[(i * 2 + 2) % 6], 0.5F, new Vector3f());
			segments.add(new LineSegment(points[i * 2], inner));
			segments.add(new LineSegment(inner, points[(i * 2 + 2) % 6]));
		}
		return segments;
	}

	private static void kochSegment(List<LineSegment> segments, Vector3f start, Vector3f end, int depth) {
		if (depth <= 0) {
			segments.add(new LineSegment(start, end));
			return;
		}
		Vector3f delta = new Vector3f(end).sub(start);
		Vector3f p1 = new Vector3f(start).add(delta.x * 0.333333F, delta.y * 0.333333F, delta.z * 0.333333F);
		Vector3f p2 = new Vector3f(start).add(delta.x * 0.666666F, delta.y * 0.666666F, delta.z * 0.666666F);
		Vector3f peak = new Vector3f(p1).lerp(p2, 0.5F, new Vector3f());
		Vector3f normal = new Vector3f(-delta.y, delta.x, 0.0F);
		if (normal.lengthSquared() < 1.0E-4F) {
			normal.set(0.0F, 0.0F, 1.0F);
		}
		normal.normalize().mul(delta.length() * 0.18F);
		peak.add(normal);
		kochSegment(segments, start, p1, depth - 1);
		kochSegment(segments, p1, peak, depth - 1);
		kochSegment(segments, peak, p2, depth - 1);
		kochSegment(segments, p2, end, depth - 1);
	}

	private static List<LineSegment> sierpinskiTetrahedron(float size, int depth) {
		List<LineSegment> segments = new ArrayList<>();
		float h = size * 0.85F;
		Vector3f a = new Vector3f(0.0F, h * 0.5F, 0.0F);
		Vector3f b = new Vector3f(-size * 0.5F, -h * 0.5F, -size * 0.28F);
		Vector3f c = new Vector3f(size * 0.5F, -h * 0.5F, -size * 0.28F);
		Vector3f d = new Vector3f(0.0F, -h * 0.5F, size * 0.55F);
		tetrahedron(segments, a, b, c, d, depth);
		return segments;
	}

	private static void tetrahedron(List<LineSegment> segments, Vector3f a, Vector3f b, Vector3f c, Vector3f d, int depth) {
		addTriangleEdges(segments, a, b, c);
		addTriangleEdges(segments, a, b, d);
		addTriangleEdges(segments, a, c, d);
		addTriangleEdges(segments, b, c, d);
		if (depth <= 1) {
			return;
		}
		Vector3f ab = midpoint(a, b);
		Vector3f ac = midpoint(a, c);
		Vector3f ad = midpoint(a, d);
		Vector3f bc = midpoint(b, c);
		Vector3f bd = midpoint(b, d);
		Vector3f cd = midpoint(c, d);
		tetrahedron(segments, a, ab, ac, ad, depth - 1);
		tetrahedron(segments, ab, b, bc, bd, depth - 1);
		tetrahedron(segments, ac, bc, c, cd, depth - 1);
		tetrahedron(segments, ad, bd, cd, d, depth - 1);
	}

	private static void addTriangleEdges(List<LineSegment> segments, Vector3f p1, Vector3f p2, Vector3f p3) {
		segments.add(new LineSegment(p1, p2));
		segments.add(new LineSegment(p2, p3));
		segments.add(new LineSegment(p3, p1));
	}

	private static Vector3f midpoint(Vector3f a, Vector3f b) {
		return new Vector3f(a).add(b).mul(0.5F);
	}

	private static void drawLine(VertexConsumer consumer, Matrix4f matrix, Vector3f from, Vector3f to, float r, float g,
			float b, float a) {
		Vector3f dir = new Vector3f(to).sub(from);
		if (dir.lengthSquared() < 1.0E-6F) {
			dir.set(0.0F, 1.0F, 0.0F);
		} else {
			dir.normalize();
		}
		consumer.vertex(matrix, from.x(), from.y(), from.z())
				.color(r, g, b, a)
				.normal(dir.x(), dir.y(), dir.z())
				.endVertex();
		consumer.vertex(matrix, to.x(), to.y(), to.z())
				.color(r, g, b, a)
				.normal(dir.x(), dir.y(), dir.z())
				.endVertex();
	}

	private record LineSegment(Vector3f from, Vector3f to) {
	}
}
