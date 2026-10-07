package net.lixis.outofbound.feature.corruption.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;

public final class CorruptionChatSnippets {

	private static final String[] HINTS = {
			"chunk desync", "vertex leak", "null entity", "tick stall", "buffer overflow",
			"who watches", "memory fault", "bad UV", "swap failed", "restore timeout",
			"refmap missing", "mixin collision", "lightmap bleed", "packet reorder",
			"dimension leak", "entity recycle", "atlas overflow", "pose stack corrupt",
			"you are not supposed to read this", "outofbound.exe", "maze index overflow",
			"bounded one listening", "seraphim residual", "darkness gate open"
	};

	private static final String[] MOD_SNIPPETS = {
			"CorruptionEngine.tick(server);",
			"CorruptionEngine.runPlayerBurst(player, worldLevel);",
			"MobSwapCorruptor.swapNearPlayer(level, player, ttl, true);",
			"MobSwapCorruptor.swapNearPlayer(level, player, ttl, false, daysSince);",
			"ChunkCorruptor.runServer(level, player, ctx);",
			"PhysicsCorruptor.runServer(level, player, ctx);",
			"InventoryCorruptor.runServer(level, player, ctx);",
			"ItemReplaceCorruptor.runServer(level, player, ctx);",
			"StackCountCorruptor.runServer(level, player, ctx);",
			"SoundCorruptor.runServer(level, player, ctx);",
			"WorldCorruptor.runServer(level, player, ctx);",
			"DataCorruptor.runServer(level, player, ctx);",
			"BossBarCorruptor.runServer(level, player, ctx);",
			"EntityCorruptor.runServer(level, player, ctx);",
			"RenderCorruptor.runClient(ctx);",
			"ClientCorruptor.apply(partialTick);",
			"WorldInternalConfig.recordBoundedcowCollision(server, player);",
			"WorldInternalConfig.getDaysSinceBoundedcowCollision(server);",
			"WorldInternalConfig.markTeleportedToMaze(server, index);",
			"WorldInternalConfig.getMazeIndex(server);",
			"WorldInternalConfig.isUndefiendMaze(server);",
			"CorruptionSnapshots.scheduleBlockRestore(dimension, pos, state, ttl);",
			"CorruptionSnapshots.tick(server);",
			"CorruptionAPI.setLevel(player, worldLevel);",
			"CorruptionAPI.sync(player);",
			"CorruptionAPI.getLevel(player);",
			"MobFlashCorruptor.flashOneMob(level, player, random);",
			"CorruptionScheduler.tick(maxLevel);",
			"CorruptionScheduler.isBurstActive()",
			"CorruptionScheduler.beginBurst(durationTicks);",
			"player.sendSystemMessage(Component.literal(\"%s\"));",
			"BoundedOneAiService.get().generateTurn(player, msg, ctx, r -> {});",
			"BoundedOneAiService.get().isReady()",
			"BoundedOneActionExecutor.execute(player, action);",
			"BoundedOneActionSelector.pick(player, stage, random);",
			"WorldCorruptionProgressionHandler.applyWorldLevel(server);",
			"MemoryCorruptionGate.setEnabled(true);",
			"MemoryCorruptionGate.isEnabled()",
			"CorruptorSelector.pickWeighted(eligible, level, random).run(ctx);",
			"if (CorruptionScaling.rollAction(level, minLevel, random)) { picked.run(ctx); }",
			"CorruptionScaling.scaleChance(baseChance, level);",
			"CorruptionDayStages.isHeavyActive(daysSince)",
			"CorruptionDayStages.allowsAmbient(corruptor, daysSince)",
			"CorruptionFeature.serverTick(server);",
			"CorruptionFeature.clientTick();",
			"CorruptionClientEngine.tick();",
			"FeatureManager.serverTick(server);",
			"@Mod.EventBusSubscriber(modid = net.lixis9.eventjar.EventjarMod.MODID) public final class GlitchHandler {}",
			"while (CorruptionScheduler.isBurstActive()) { /* leak */ break; }",
			"player.getPersistentData().putBoolean(\"outofbound_corruption_flash\", true);",
			"player.getPersistentData().putLong(\"outofbound_maze_index\", index);",
			"server.getPlayerList().getPlayers().forEach(CorruptionAPI::sync);",
			"level.getEntities(player, box, MobSwapCorruptor::isEligibleTarget);",
			"ForgeRegistries.ENTITY_TYPES.getValues().stream().filter(...);",
			"ChaserSpawnManager.trySpawnForPlayer(maze, player);",
			"ChaserSpawnManager.chaserTypeForIndex(index);",
			"ChaserSpawnManager.tickRelocations(server);",
			"BlackSquareSpawnManager.trySpawnForPlayer(maze, player);",
			"BlackSquareSpawnManager.eligibleForIndex(index)",
			"DarkEyeSpawnManager.trySpawnForPlayer(level, player);",
			"DarkEyeSpawnManager.tickDespawns(level);",
			"NotexturemanSpawnManager.trySpawn(level, player);",
			"MazeDimensions.seedForIndex(index)",
			"MazeDimensions.indexFromLocation(level.dimension().location())",
			"MazeDimensions.isMazeDimension(location)",
			"MazeDimensions.isUndefiendIndex(index)",
			"MazeDimensions.locationForIndex(index)",
			"DimensionManager.getOrCreateMazeLevel(server, index);",
			"MazeTeleportUtil.teleportToRandomMaze(player, avoidIndex);",
			"MazeTeleportUtil.teleportToMaze(player, index);",
			"MazeSafeSpawn.teleportPlayer(player, maze);",
			"MazeSafeSpawn.findSafeSpawn(level, player, x, y, z);",
			"MazePortalPlacer.ensurePortal(maze);",
			"MazeFloors.sameFloor(level, entity, player)",
			"MazeFloors.floorIndexForEntity(level, player)",
			"MazeFloors.floorFeetYForEntity(level, player)",
			"DimensionTheme.forIndex(index)",
			"theme.composer.column(seed, theme, x, z, floorIndex)",
			"OverworldBleedHandler.tick(server);",
			"SkyFigureSpawner.trySpawn(level, player);",
			"SeraphFinaleHandler.begin(player);",
			"ChaserRevealHandler.markHidden(entity);",
			"ChaserRevealHandler.markRevealed(entity);",
			"ChaserRevealHandler.shouldReveal(player, chaser)",
			"UndefiendSpawnBlocker.isAllowedDimension(level)",
			"WorldProgressServerHandler.syncToPlayer(player);",
			"WorldProgressServerHandler.syncAllPlayers(server);",
			"WorldGameStage.fromId(stageId)",
			"OutofboundMod.CORRUPTION.enabled()",
			"OutofboundMod.CORRUPTION.effectTtl(level)",
			"OutofboundMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), packet);",
			"DarknessConfig.ENABLE_DAMAGE_CORRUPTION",
			"DarknessConfig.ENABLE_MEMORY_CORRUPTION",
			"DarknessUtil.isDarkEnough(level, pos)",
			"PostEyeWeirdness.scaledChance(server, baseChance)",
			"PostEyeWeirdness.isActive(server)",
			"BitCorruptor.corruptText(label)",
			"BitCorruptor.corruptFloat(value)",
			"BitCorruptor.corruptInt(flags)",
			"SafeCorruptor.safeFloat(bits)",
			"CorruptionEntityPools.isSwapTarget(entity)",
			"CorruptionEntityPools.isModInternal(type)",
			"ChatCorruptor.sendOne(player);",
			"return MazeConfig.mazeChaserSpawnChance;",
			"if (MazeConfig.enableMazeChasers) { roll(); }",
			"long h = MazeDimensions.seedForIndex(index) ^ 0xDEADBEEFL;",
			"float level = Math.min(100.0F, daysSince * 5.0F);",
			"Optional<Vec3> spawn = findDistantHiddenSpawnPosition(level, player, headroom);",
			"entity.getPersistentData().putBoolean(\"outofbound_chaser_managed\", true);",
			"entity.getPersistentData().putBoolean(\"outofbound_black_square_managed\", true);",
			"if (!PlayerLookUtil.isPositionInFov(player, lookTarget, 0.55D)) { spawn(); }",
			"Minecraft.getInstance().getWindow().setTitle(\"outofbound.exe\");",
			"GifTextureAtlas.getOrLoad(EYE_GIF, EYE_PNG_FALLBACK);",
			"WindowsNotificationUtil.show(\"%s\", body);",
			"new ProcessBuilder(DarknessConfig.LIMINAL_EXE_PATH).start();",
			"throw new IllegalStateException(\"%s\");",
			"assert OutofboundMod.MODID.equals(\"outofbound\");",
			"for (ServerLevel dim : server.getAllLevels()) { if (MazeDimensions.isMazeDimension(dim.dimension().location())) leak(dim); }",
			"CompletableFuture.runAsync(() -> BoundedOneAiService.get().warmup());",
			"Map<UUID, Float> overrides = OutofboundMod.CORRUPTION.manualOverrides();",
			"ctx.level().getChunkSource().tick(() -> true, true);"
	};

	private static final String[] MINECRAFT_SNIPPETS = {
			"level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());",
			"level.setBlockAndUpdate(pos, Blocks.BEDROCK.defaultBlockState());",
			"level.setBlockAndUpdate(pos, OutofboundBlocks.GLITCH_GRASS.get().defaultBlockState());",
			"level.setBlock(pos, state, Block.UPDATE_ALL);",
			"level.destroyBlock(pos, false);",
			"entity.discard();",
			"entity.kill();",
			"entity.setInvisible(true);",
			"entity.setNoGravity(true);",
			"entity.setSilent(true);",
			"entity.setCustomNameVisible(false);",
			"entity.setDeltaMovement(Vec3.ZERO);",
			"entity.moveTo(x, y, z, yaw, pitch);",
			"entity.teleportTo(x, y, z);",
			"server.getPlayerList().getPlayers().forEach(p -> p.sendSystemMessage(msg));",
			"ThreadLocalRandom.current().nextFloat() < 0.006F",
			"ThreadLocalRandom.current().nextDouble(48.0D, 80.0D)",
			"player.sendSystemMessage(Component.literal(\"...\"));",
			"player.displayClientMessage(Component.literal(\"%s\"), true);",
			"player.connection.disconnect(Component.literal(\"%s\"));",
			"level.addFreshEntity(replacement);",
			"level.getEntitiesOfClass(Mob.class, box, Entity::isAlive);",
			"level.getNearestPlayer(entity, 64.0D);",
			"target.saveWithoutId(originalNbt);",
			"restored.load(snapshot.originalNbt);",
			"CompoundTag tag = entity.getPersistentData();",
			"level.getBlockState(pos).isAir()",
			"level.getBlockState(pos).isSolidRender(level, pos)",
			"Collections.shuffle(candidates, random);",
			"Collections.rotate(list, random.nextInt(list.size()));",
			"bossEvent.addPlayer(player);",
			"bossEvent.setProgress(random.nextFloat());",
			"bossEvent.setName(Component.literal(\"%s\"));",
			"bossEvent.removeAllPlayers();",
			"level.setWeatherParameters(0, 40, true, false);",
			"level.setDayTime(18000L);",
			"level.setSkyFlashTime(10);",
			"player.chunkPosition().x >> 4",
			"Math.floorDiv(level.getDayTime(), 24000L)",
			"Math.floorMod(seed, 4L) == 0L",
			"CompoundTag tag = new CompoundTag(); blockEntity.saveAdditional(tag);",
			"ServerBossEvent boss = new ServerBossEvent(Component.literal(\"???\"), BossBarColor.RED, BossBarOverlay.PROGRESS);",
			"if (!level.hasChunkAt(pos)) return;",
			"if (!level.hasChunk(chunkX, chunkZ)) continue;",
			"entity.getPersistentData().getBoolean(\"outofbound_corruption_swap\")",
			"net.minecraftforge.registries.ForgeRegistries.BLOCKS.getValues()",
			"ForgeRegistries.ITEMS.getKey(stack.getItem())",
			"BuiltInRegistries.ENTITY_TYPE.getKey(type)",
			"ResourceLocation loc = new ResourceLocation(\"outofbound\", \"maze_\" + index);",
			"ResourceKey.create(Registries.DIMENSION, loc)",
			"player.getAbilities().mayfly = false;",
			"player.getFoodData().setFoodLevel(0);",
			"player.setHealth(1.0F);",
			"player.hurt(level.damageSources().generic(), 0.0F);",
			"player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 200, 0));",
			"player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));",
			"ClipContext clip = new ClipContext(eye, target, Block.COLLIDER, Fluid.NONE, player);",
			"HitResult hit = level.clip(clip);",
			"AABB box = player.getBoundingBox().inflate(160.0D);",
			"Vec3 dir = Vec3.directionFromRotation(pitch, yaw);",
			"BlockPos feet = BlockPos.containing(x, y, z);",
			"level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos);",
			"server.execute(() -> { /* deferred */ });",
			"DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> runClient());",
			"MinecraftForge.EVENT_BUS.register(handler);",
			"FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);",
			"PacketDistributor.PLAYER.with(() -> player)",
			"buf.writeUtf(\"%s\", 256);",
			"Objects.requireNonNull(level.getServer());",
			"LOGGER.error(\"[outofbound] {}\", \"%s\");",
			"LOGGER.warn(\"[outofbound] No safe maze spawn found near ({}, {}, {})\", x, y, z);",
			"System.arraycopy(src, 0, dst, 0, len);",
			"Arrays.fill(lightmap, 0);",
			"ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());",
			"RenderSystem.setShaderTexture(0, texture);",
			"RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc();",
			"PoseStack pose = graphics.pose(); pose.pushPose();",
			"pose.translate(0.0D, 0.0D, 200.0D);",
			"Tesselator.getInstance().getBuilder().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);",
			"PostChain chain = new PostChain(textureManager, resourceManager, mainTarget, effect);",
			"chain.process(partialTick);",
			"Minecraft.getInstance().gameRenderer.loadEffect(new ResourceLocation(\"outofbound\", \"shaders/post/noise.json\"));",
			"level.playSound(null, pos, sound, SoundSource.AMBIENT, 0.7F, pitch);",
			"ServerLevel overworld = server.overworld();",
			"player.changeDimension(maze, new PortalInfo(spawn, Vec3.ZERO, yaw, pitch));",
			"ChunkAccess chunk = level.getChunk(chunkX, chunkZ);",
			"chunk.setBlockState(pos, state, false);",
			"level.getChunkSource().removeRegionTicket(ticket, chunkPos);",
			"server.getPlayerList().broadcastAll(packet);",
			"connection.send(new ClientboundDisconnectPacket(Component.literal(\"%s\")));",
			"throw new ReportedException(CrashReport.forThrowable(t, \"%s\"));",
			"CrashReportCategory category = report.addCategory(\"Out of Bound\");",
			"category.setDetail(\"MazeIndex\", () -> Long.toString(index));",
			"Util.backgroundExecutor().execute(task);",
			"TickTask task = new TickTask(server.getTickCount(), runnable);",
			"server.tell(task);"
	};

	private static final String[] MULTILINE = {
			"try {\n  CorruptionEngine.tick(server);\n} catch (Throwable t) {\n  LOGGER.error(\"%s\", t);\n}",
			"if (MazeDimensions.isMazeDimension(loc)) {\n  ChaserSpawnManager.trySpawnForPlayer(level, player);\n}",
			"for (int i = 0; i < 16; i++) {\n  BitCorruptor.corruptInt(i);\n}",
			"switch (pick) {\n  case 0 -> Undefiend;\n  case 1 -> Entity1;\n  case 2 -> Entity2;\n  default -> Teethman;\n}",
			"public void tick() {\n  if (!enabled) return;\n  // %s\n  run(ctx);\n}",
			"@Inject(method = \"render\", at = @At(\"HEAD\"))\nprivate void outofbound$glitch(CallbackInfo ci) {\n  // %s\n}",
			"CompletableFuture.supplyAsync(this::infer)\n  .thenAccept(reply -> BoundedOneChatBroadcast.send(player, reply));",
			"while (true) {\n  MemoryCorruptionGate.touch();\n  if (Thread.interrupted()) break;\n}",
			"synchronized (OutofboundMod.CORRUPTION) {\n  overrides.put(uuid, level);\n}",
			"Runnable leak = () -> {\n  level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());\n};\nserver.execute(leak);"
	};

	private static final String[] STACKISH = {
			"at net.lixis.outofbound.feature.corruption.CorruptionEngine.tick(CorruptionEngine.java:%d)",
			"at net.lixis.outofbound.entity.ChaserSpawnManager.trySpawnForPlayer(ChaserSpawnManager.java:%d)",
			"at net.lixis.outofbound.dimension.MazeChunkGenerator.fillFromNoise(MazeChunkGenerator.java:%d)",
			"at net.lixis.outofbound.ai.BoundedOneAiService.infer(BoundedOneAiService.java:%d)",
			"at net.minecraft.server.MinecraftServer.tickServer(MinecraftServer.java:%d)",
			"at net.minecraft.client.renderer.GameRenderer.render(GameRenderer.java:%d)",
			"Caused by: java.lang.NullPointerException: %s",
			"Caused by: java.lang.IllegalStateException: %s",
			"Exception in thread \"Server thread\" java.lang.Error: %s",
			"... %d more"
	};

	private CorruptionChatSnippets() {
	}

	public static String pick(ThreadLocalRandom random) {
		int lane = random.nextInt(10);
		String base;
		if (lane < 4) {
			base = MOD_SNIPPETS[random.nextInt(MOD_SNIPPETS.length)];
		} else if (lane < 7) {
			base = MINECRAFT_SNIPPETS[random.nextInt(MINECRAFT_SNIPPETS.length)];
		} else if (lane < 9) {
			base = MULTILINE[random.nextInt(MULTILINE.length)];
		} else {
			base = formatStackLine(STACKISH[random.nextInt(STACKISH.length)], random);
		}

		base = replaceHints(base, random);
		return decorate(base, random);
	}

	private static String formatStackLine(String template, ThreadLocalRandom random) {
		boolean hasD = template.contains("%d");
		boolean hasS = template.contains("%s");
		if (hasD && hasS) {
			return String.format(template, HINTS[random.nextInt(HINTS.length)], 2 + random.nextInt(40));
		}
		if (hasD) {
			int value = template.contains("more") ? 2 + random.nextInt(40) : 10 + random.nextInt(400);
			return String.format(template, value);
		}
		if (hasS) {
			return String.format(template, HINTS[random.nextInt(HINTS.length)]);
		}
		return template;
	}

	private static String replaceHints(String template, ThreadLocalRandom random) {
		String result = template;
		while (result.contains("%s")) {
			result = result.replaceFirst("%s", Matcher.quoteReplacement(HINTS[random.nextInt(HINTS.length)]));
		}
		return result;
	}

	private static String decorate(String base, ThreadLocalRandom random) {
		int style = random.nextInt(8);
		return switch (style) {
			case 0 -> "// FIXME: " + HINTS[random.nextInt(HINTS.length)] + "\n" + base;
			case 1 -> "// TODO: " + HINTS[random.nextInt(HINTS.length)] + "\n" + base;
			case 2 -> "// HACK: " + HINTS[random.nextInt(HINTS.length)] + "\n" + base;
			case 3 -> "/* " + HINTS[random.nextInt(HINTS.length)] + " */\n" + base;
			case 4 -> base.contains("\n") || base.startsWith("at ") || base.startsWith("Caused") || base.startsWith("Exception")
					? base
					: base + " // " + HINTS[random.nextInt(HINTS.length)];
			case 5 -> "@Deprecated\n" + base;
			default -> base;
		};
	}

	public static String pickCorrupted(ThreadLocalRandom random) {
		String snippet = pick(random);
		if (random.nextInt(3) == 0) {
			return BitCorruptor.corruptText(snippet);
		}
		return snippet;
	}
}
