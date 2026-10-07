package net.lixis.outofbound.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class BedrockAnimationPlayer {

	private static final float DEG = (float) (Math.PI / 180.0F);
	private static BedrockAnimationPlayer instance;

	private final Map<String, AnimationClip> clips = new HashMap<>();

	private BedrockAnimationPlayer() {
	}

	public static BedrockAnimationPlayer get() {
		if (instance == null) {
			instance = new BedrockAnimationPlayer();
		}
		return instance;
	}

	public void load(ResourceManager resourceManager) {
		clips.clear();
		ResourceLocation file = new ResourceLocation("outofbound", "animations/undefiend.animation.json");
		resourceManager.getResource(file).ifPresent(resource -> {
			try (InputStreamReader reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {
				JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
				JsonObject animations = root.getAsJsonObject("animations");
				for (Map.Entry<String, JsonElement> entry : animations.entrySet()) {
					clips.put(entry.getKey(), parseClip(entry.getValue().getAsJsonObject()));
				}
			} catch (Exception ignored) {
			}
		});
	}

	public boolean hasClip(String name) {
		return clips.containsKey(name);
	}

	public void apply(String clipName, Map<String, ModelPart> bones, Map<String, BonePose> defaultPoses, float timeSeconds) {
		AnimationClip clip = clips.get(clipName);
		if (clip == null) {
			return;
		}

		float length = clip.length > 0.0F ? clip.length : 0.5F;
		float time = clip.loop ? timeSeconds % length : Math.min(timeSeconds, length);

		for (BoneTrack track : clip.bones.values()) {
			ModelPart part = bones.get(track.boneName);
			BonePose defaultPose = defaultPoses.get(track.boneName);
			if (part == null || defaultPose == null) {
				continue;
			}

			if (track.rotation != null) {
				float[] rotation = track.rotation.sample(time);
				part.xRot = defaultPose.xRot + rotation[0] * DEG;
				part.yRot = defaultPose.yRot + rotation[1] * DEG;
				part.zRot = defaultPose.zRot + rotation[2] * DEG;
			}

			if (track.position != null) {
				float[] position = track.position.sample(time);
				part.x = defaultPose.x + position[0];
				part.y = defaultPose.y + position[1];
				part.z = defaultPose.z + position[2];
			}
		}
	}

	public static Map<String, BonePose> captureDefaultPose(Map<String, ModelPart> bones) {
		Map<String, BonePose> poses = new HashMap<>();
		for (Map.Entry<String, ModelPart> entry : bones.entrySet()) {
			poses.put(entry.getKey(), BonePose.from(entry.getValue()));
		}
		return poses;
	}

	public static final class BonePose {
		public final float x;
		public final float y;
		public final float z;
		public final float xRot;
		public final float yRot;
		public final float zRot;

		private BonePose(float x, float y, float z, float xRot, float yRot, float zRot) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.xRot = xRot;
			this.yRot = yRot;
			this.zRot = zRot;
		}

		public static BonePose from(ModelPart part) {
			return new BonePose(part.x, part.y, part.z, part.xRot, part.yRot, part.zRot);
		}
	}

	private static AnimationClip parseClip(JsonObject object) {
		AnimationClip clip = new AnimationClip();
		clip.loop = !object.has("loop") || object.get("loop").getAsBoolean();
		clip.length = object.has("animation_length") ? object.get("animation_length").getAsFloat() : 0.5F;

		if (!object.has("bones")) {
			return clip;
		}

		JsonObject bones = object.getAsJsonObject("bones");
		for (Map.Entry<String, JsonElement> entry : bones.entrySet()) {
			JsonObject boneObject = entry.getValue().getAsJsonObject();
			BoneTrack track = new BoneTrack(entry.getKey());

			if (boneObject.has("rotation")) {
				track.rotation = parseVectorTrack(boneObject.get("rotation"));
			}
			if (boneObject.has("position")) {
				track.position = parseVectorTrack(boneObject.get("position"));
			}

			clip.bones.put(entry.getKey(), track);
		}

		return clip;
	}

	private static VectorTrack parseVectorTrack(JsonElement element) {
		if (element.isJsonArray()) {
			VectorTrack track = new VectorTrack();
			track.staticValue = readVector(element.getAsJsonArray());
			return track;
		}

		VectorTrack track = new VectorTrack();
		JsonObject keyframes = element.getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : keyframes.entrySet()) {
			float time = Float.parseFloat(entry.getKey());
			track.keyframes.put(time, readVector(entry.getValue().getAsJsonArray()));
		}
		return track;
	}

	private static float[] readVector(JsonArray array) {
		return new float[] {
				array.get(0).getAsFloat(),
				array.size() > 1 ? array.get(1).getAsFloat() : 0.0F,
				array.size() > 2 ? array.get(2).getAsFloat() : 0.0F
		};
	}

	private static final class AnimationClip {
		private boolean loop = true;
		private float length = 0.5F;
		private final Map<String, BoneTrack> bones = new HashMap<>();
	}

	private static final class BoneTrack {
		private final String boneName;
		private VectorTrack rotation;
		private VectorTrack position;

		private BoneTrack(String boneName) {
			this.boneName = boneName;
		}
	}

	private static final class VectorTrack {
		private float[] staticValue;
		private final Map<Float, float[]> keyframes = new HashMap<>();

		private float[] sample(float time) {
			if (staticValue != null) {
				return staticValue;
			}
			if (keyframes.isEmpty()) {
				return new float[] {0.0F, 0.0F, 0.0F};
			}

			float previousTime = Float.NEGATIVE_INFINITY;
			float nextTime = Float.POSITIVE_INFINITY;
			for (float keyTime : keyframes.keySet()) {
				if (keyTime <= time && keyTime >= previousTime) {
					previousTime = keyTime;
				}
				if (keyTime >= time && keyTime <= nextTime) {
					nextTime = keyTime;
				}
			}

			if (previousTime == Float.NEGATIVE_INFINITY) {
				return keyframes.get(nextTime);
			}
			if (nextTime == Float.POSITIVE_INFINITY || previousTime == nextTime) {
				return keyframes.get(previousTime);
			}

			float alpha = (time - previousTime) / (nextTime - previousTime);
			float[] from = keyframes.get(previousTime);
			float[] to = keyframes.get(nextTime);
			return new float[] {
					lerp(from[0], to[0], alpha),
					lerp(from[1], to[1], alpha),
					lerp(from[2], to[2], alpha)
			};
		}

		private static float lerp(float from, float to, float alpha) {
			return from + (to - from) * alpha;
		}
	}

	public static Map<String, ModelPart> buildBoneMap(Modelunknown<?> model) {
		Map<String, ModelPart> bones = new HashMap<>();
		bones.put("base", model.base);
		bones.put("lowerbody", model.lowerbody);
		bones.put("upperbody", model.upperbody);
		bones.put("neck", model.neck);
		bones.put("head", model.head);
		bones.put("jaw", model.jaw);
		bones.put("rightupperarm", model.rightupperarm);
		bones.put("rightlowerarm", model.rightlowerarm);
		bones.put("righthand", model.righthand);
		bones.put("leftupperarm", model.leftupperarm);
		bones.put("leftlowerarm", model.leftlowerarm);
		bones.put("lowerlowerLeftArm", model.lowerlowerLeftArm);
		bones.put("lefthand", model.lefthand);
		bones.put("upperleftleg", model.upperleftleg);
		bones.put("lowerleftleg", model.lowerleftleg);
		bones.put("leftfoot", model.leftfoot);
		bones.put("upperrightleg", model.upperrightleg);
		bones.put("lowerrightleg", model.lowerrightleg);
		bones.put("rightfoot", model.rightfoot);
		return bones;
	}

	public static String clip(String name) {
		return "animation.undefiend." + name.toLowerCase(Locale.ROOT);
	}
}
