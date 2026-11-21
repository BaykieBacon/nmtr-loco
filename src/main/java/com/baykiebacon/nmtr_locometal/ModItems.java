package com.baykiebacon.nmtr_locometal;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModItems {
	private static final String REGISTRY_RESOURCE = "/assets/nmtr-locometal/block_registry.txt";
	private static final Map<String, Block> BLOCKS = new LinkedHashMap<>();

	private ModItems() {
	}

	public static void initialize() {
		try (InputStream input = ModItems.class.getResourceAsStream(REGISTRY_RESOURCE)) {
			if (input == null) {
				throw new IllegalStateException("Missing generated block registry: " + REGISTRY_RESOURCE);
			}

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
				String blockId;
				while ((blockId = reader.readLine()) != null) {
					if (!blockId.isBlank()) {
						Block block = Registry.register(BuiltInRegistries.BLOCK, NmtrLocometal.id(blockId), LocometalBlocks.create(blockId));
						Registry.register(BuiltInRegistries.ITEM, NmtrLocometal.id(blockId), new BlockItem(block, new Item.Properties()));
						BLOCKS.put(blockId, block);
					}
				}
			}
		} catch (IOException exception) {
			throw new UncheckedIOException("Could not load generated block registry", exception);
		}

		if (BLOCKS.isEmpty()) {
			throw new IllegalStateException("No blockstates were found for nmtr-locometal");
		}

		Block tabIcon = BLOCKS.getOrDefault("locometal_boiler", BLOCKS.values().iterator().next());
		CreativeModeTab tab = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
			.title(Component.translatable("itemGroup.nmtr-locometal.content"))
			.icon(() -> new ItemStack(tabIcon))
			.displayItems((parameters, output) -> BLOCKS.values().forEach(block -> output.accept(new ItemStack(block))))
			.build();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, NmtrLocometal.id("content"), tab);
	}

	public static Map<String, Block> registeredBlocks() {
		return Collections.unmodifiableMap(BLOCKS);
	}
}