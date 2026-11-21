package com.baykiebacon.nmtr_locometal.client;

import com.baykiebacon.nmtr_locometal.LocometalBlocks;
import com.baykiebacon.nmtr_locometal.NmtrLocometal;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.connected.CTTypeRegistry;
import com.simibubi.create.foundation.block.connected.SimpleCTBehaviour;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Map;
import java.util.Set;

import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour.CTContext;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour.ContextRequirement;

final class LocometalConnectedTextures {
	private static final Set<String> COLORS = Set.of("black", "blue", "brown", "chartreuse", "cyan", "diorite", "dripstone", "granite", "gray", "green", "light_blue", "light_gray", "lime", "limestone", "magenta", "maroon", "netherite", "ochrum", "olive_green", "orange", "pine_green", "pink", "purple", "red", "royal_blue", "scorchia", "sea_green", "tuff", "turquoise", "vermilion", "white", "yellow");
	private static final CTType VERTICAL_PINKMACHINE = new PinkmachineCTType();

	private LocometalConnectedTextures() {
	}

	static void register(Map<String, Block> blocks) {
		CTTypeRegistry.register(VERTICAL_PINKMACHINE);
		blocks.forEach((id, block) -> {
			register(id, block);
			if (id.endsWith("_locometal_window") || id.endsWith("_locometal_end_ladder") || id.endsWith("_locometal_rung_ladder")) {
				BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutoutMipped());
			}
		});
	}

	private static void register(String id, Block block) {
		String texture = connectedTexture(id);
		if (texture == null) return;

		String texturePath = "block/palettes/" + paletteColor(id) + "/" + texture;
		CTType type = connectedType(id);
		CTSpriteShiftEntry shift = CTSpriteShifter.getCT(
			type,
			NmtrLocometal.id(texturePath),
			NmtrLocometal.id(texturePath + "_connected")
		);

		ConnectedTextureBehaviour behaviour;
		if (id.endsWith("_locometal_boiler")) {
			behaviour = new BoilerBehaviour(shift);
		} else if (id.endsWith("_locometal_pillar") || id.endsWith("_locometal_smokebox")) {
			behaviour = new PillarBehaviour(shift);
		} else if (id.endsWith("_locometal_window")) {
			behaviour = new SimpleCTBehaviour(shift);
		} else {
			behaviour = new SimpleCTBehaviour(shift);
		}

		CreateRegistrate.connectedTextures(() -> behaviour).accept(block);
	}

	private static String paletteColor(String id) {
		return COLORS.stream()
			.filter(color -> id.startsWith(color + "_"))
			.max(java.util.Comparator.comparingInt(String::length))
			.orElse("netherite");
	}

	private static String connectedTexture(String id) {
		if (id.endsWith("_flat_riveted_locometal") || id.endsWith("_flat_slashed_locometal")) return null;
		if (id.endsWith("_riveted_locometal")) return "riveted";
		if (id.endsWith("_slashed_locometal")) return "slashed";
		if (id.equals("brass_wrapped_locometal") || id.endsWith("_brass_wrapped_locometal")) return "wrapped_slashed";
		if (id.equals("copper_wrapped_locometal") || id.endsWith("_copper_wrapped_locometal")) return "copper_wrapped_slashed";
		if (id.equals("iron_wrapped_locometal") || id.endsWith("_iron_wrapped_locometal")) return "iron_wrapped_slashed";
		if (id.endsWith("_locometal_vent")) return "vent";
		if (id.endsWith("_locometal_pillar")) return "riveted_pillar_side";
		if (id.endsWith("_locometal_smokebox")) {
			if (id.contains("_copper_wrapped_")) return "copper_wrapped_tank_side";
			if (id.contains("_iron_wrapped_")) return "iron_wrapped_tank_side";
			if (id.contains("_brass_wrapped_") || id.startsWith("wrapped_")) return "wrapped_tank_side";
			return "tank_side";
		}
		if (id.endsWith("_locometal_window")) {
			if (id.contains("four_pane")) return "four_pane_window";
			if (id.contains("two_pane")) return "two_pane_window";
			if (id.contains("single_pane")) return "single_pane_window";
			return "round_pane_window";
		}
		if (id.endsWith("_locometal_boiler")) {
			if (id.contains("copper_wrapped")) return "copper_wrapped_boiler_side";
			if (id.contains("iron_wrapped")) return "iron_wrapped_boiler_side";
			if (id.contains("brass_wrapped")) return "wrapped_boiler_side";
			return "boiler_side";
		}
		return null;
	}

	private static CTType connectedType(String id) {
		if (id.endsWith("_locometal_boiler")) return AllCTTypes.HORIZONTAL_KRYPPERS;
		if (id.endsWith("_locometal_smokebox")) return VERTICAL_PINKMACHINE;
		if (id.endsWith("_locometal_pillar")) return AllCTTypes.VERTICAL;
		if (id.endsWith("_locometal_window")) return AllCTTypes.OMNIDIRECTIONAL;
		return AllCTTypes.OMNIDIRECTIONAL;
	}

	private static final class PillarBehaviour extends ConnectedTextureBehaviour.Base {
		private final CTSpriteShiftEntry shift;

		private PillarBehaviour(CTSpriteShiftEntry shift) {
			this.shift = shift;
		}

		@Override
		protected Direction getUpDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
			if (state.hasProperty(BlockStateProperties.FACING)) return state.getValue(BlockStateProperties.FACING);
			if (state.hasProperty(BlockStateProperties.AXIS)) {
				Axis axis = state.getValue(BlockStateProperties.AXIS);
				return Direction.fromAxisAndDirection(axis, axis == Axis.Z ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE);
			}
			return super.getUpDirection(reader, pos, state, face);
		}

		@Override
		protected Direction getRightDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
			Direction facing = state.hasProperty(BlockStateProperties.FACING)
				? state.getValue(BlockStateProperties.FACING)
				: Direction.fromAxisAndDirection(state.getValue(BlockStateProperties.AXIS), state.getValue(BlockStateProperties.AXIS) == Axis.Z ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE);
			return facing.getAxis().isVertical() ? Direction.fromAxisAndDirection(Axis.X, face.getAxisDirection()) : facing.getClockWise();
		}

		@Override
		public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
			if (state.getBlock() != other.getBlock()) return false;
			if (state.hasProperty(BlockStateProperties.FACING)) return other.hasProperty(BlockStateProperties.FACING) && state.getValue(BlockStateProperties.FACING) == other.getValue(BlockStateProperties.FACING);
			return !state.hasProperty(BlockStateProperties.AXIS) || other.hasProperty(BlockStateProperties.AXIS) && state.getValue(BlockStateProperties.AXIS) == other.getValue(BlockStateProperties.AXIS);
		}

		@Override
		protected boolean reverseUVsVertically(BlockState state, Direction face) {
			if (face != Direction.DOWN) return false;
			if (state.hasProperty(BlockStateProperties.FACING)) return state.getValue(BlockStateProperties.FACING).getAxis().isHorizontal();
			return state.hasProperty(BlockStateProperties.AXIS) && state.getValue(BlockStateProperties.AXIS).isHorizontal();
		}

		@Override
		public CTSpriteShiftEntry getShift(BlockState state, Direction direction, TextureAtlasSprite sprite) {
			if (state.hasProperty(BlockStateProperties.FACING)) return state.getValue(BlockStateProperties.FACING).getAxis() != direction.getAxis() ? shift : null;
			return state.hasProperty(BlockStateProperties.AXIS) && state.getValue(BlockStateProperties.AXIS) != direction.getAxis() ? shift : null;
		}
	}

	private static final class BoilerBehaviour extends ConnectedTextureBehaviour.Base {
		private final CTSpriteShiftEntry shift;

		private BoilerBehaviour(CTSpriteShiftEntry shift) {
			this.shift = shift;
		}

		@Override
		public CTSpriteShiftEntry getShift(BlockState state, Direction direction, TextureAtlasSprite sprite) {
			return shift;
		}

		@Override
		public CTContext buildContext(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face, ContextRequirement requirement) {
			CTContext context = new CTContext();
			Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
			context.right = matches(reader, pos, state, Direction.fromAxisAndDirection(axis, AxisDirection.POSITIVE));
			context.left = matches(reader, pos, state, Direction.fromAxisAndDirection(axis, AxisDirection.NEGATIVE));
			return context;
		}

		private boolean matches(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction offset) {
			BlockState other = reader.getBlockState(pos.relative(offset));
			return other.getBlock() == state.getBlock()
				&& other.getValue(BlockStateProperties.HORIZONTAL_AXIS) == state.getValue(BlockStateProperties.HORIZONTAL_AXIS)
				&& other.getValue(LocometalBlocks.raisedProperty()) == state.getValue(LocometalBlocks.raisedProperty());
		}
	}

	private static final class PinkmachineCTType implements CTType {
		private static final ResourceLocation ID = NmtrLocometal.id("vertical_pinkmachine");
		private static final ContextRequirement REQUIREMENT = ContextRequirement.builder().vertical().build();

		@Override
		public ResourceLocation getId() {
			return ID;
		}

		@Override
		public int getSheetSize() {
			return 2;
		}

		@Override
		public ContextRequirement getContextRequirement() {
			return REQUIREMENT;
		}

		@Override
		public int getTextureIndex(CTContext context) {
			if (!context.up && !context.down) return 0;
			if (!context.up) return 3;
			if (!context.down) return 2;
			return 1;
		}
	}
}