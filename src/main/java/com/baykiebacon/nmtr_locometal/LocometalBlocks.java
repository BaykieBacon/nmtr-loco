package com.baykiebacon.nmtr_locometal;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.LadderBlock;

public final class LocometalBlocks {
	private static final BooleanProperty WINDOWED = BooleanProperty.create("windowed");
	private static final BooleanProperty VISIBLE = BooleanProperty.create("visible");
	private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final EnumProperty<BoilerStyle> STYLE = EnumProperty.create("style", BoilerStyle.class);
	private static final EnumProperty<Direction.Axis> HORIZONTAL_AXIS = BlockStateProperties.HORIZONTAL_AXIS;
	private static final BooleanProperty RAISED = BooleanProperty.create("raised");

	public static BooleanProperty raisedProperty() {
		return RAISED;
	}

	private LocometalBlocks() {
	}

	static Block create(String id) {
		BlockBehaviour.Properties properties = BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK);
		if (id.endsWith("_locometal_boiler")) return new Boiler(properties);
		if (id.endsWith("_locometal_smokebox")) return new FacingBlock(properties);
		if (id.endsWith("_hinged_locometal_door")) return new HingedDoor(properties);
		if (id.endsWith("_sliding_locometal_door") || id.endsWith("_folding_locometal_door")) return new MovingDoor(properties);
		if (id.endsWith("_locometal_trapdoor")) return new WindowedTrapdoor(properties);
		if (id.endsWith("_locometal_window")) return new WindowBlock(BlockBehaviour.Properties.copy(Blocks.GLASS));
		if (id.endsWith("_locometal_flywheel")) return new FlywheelBlock(properties.noOcclusion());
		if (id.endsWith("_locometal_pillar")) return new RotatedPillarBlock(properties);
		if (id.endsWith("_locometal_end_ladder") || id.endsWith("_locometal_rung_ladder")) return new LocometalLadder(properties.noOcclusion());
		return new Block(properties);
	}

	private static final class Boiler extends Block {
		private Boiler(Properties properties) {
			super(properties);
			registerDefaultState(defaultBlockState().setValue(STYLE, BoilerStyle.FLAT).setValue(HORIZONTAL_AXIS, Direction.Axis.X).setValue(RAISED, false));
		}

		@Override
		protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
			builder.add(STYLE, HORIZONTAL_AXIS, RAISED);
		}
	}

	private static final class FacingBlock extends Block {
		private FacingBlock(Properties properties) {
			super(properties);
			registerDefaultState(defaultBlockState().setValue(BlockStateProperties.FACING, Direction.NORTH));
		}

		@Override
		protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
			builder.add(BlockStateProperties.FACING);
		}
	}

	private static final class HingedDoor extends DoorBlock {
		private HingedDoor(Properties properties) {
			super(properties, BlockSetType.OAK);
			registerDefaultState(defaultBlockState().setValue(WINDOWED, false));
		}

		@Override
		protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
			super.createBlockStateDefinition(builder);
			builder.add(WINDOWED);
		}
	}

	private static final class MovingDoor extends DoorBlock {
		private MovingDoor(Properties properties) {
			super(properties, BlockSetType.OAK);
			registerDefaultState(defaultBlockState().setValue(WINDOWED, false).setValue(VISIBLE, true));
		}

		@Override
		protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
			super.createBlockStateDefinition(builder);
			builder.add(WINDOWED, VISIBLE);
		}
	}

	private static final class WindowedTrapdoor extends TrapDoorBlock {
		private WindowedTrapdoor(Properties properties) {
			super(properties, BlockSetType.OAK);
			registerDefaultState(defaultBlockState().setValue(WINDOWED, false));
		}

		@Override
		protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
			super.createBlockStateDefinition(builder);
			builder.add(WINDOWED);
		}
	}

	private static final class FacingWaterloggedBlock extends Block {
		private FacingWaterloggedBlock(Properties properties) {
			super(properties);
			registerDefaultState(defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).setValue(WATERLOGGED, false));
		}

		@Override
		protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
			builder.add(BlockStateProperties.HORIZONTAL_FACING, WATERLOGGED);
		}
	}

	private static final class WindowBlock extends RotatedPillarBlock {
		private WindowBlock(Properties properties) {
			super(properties);
		}

		@Override
		public boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
			return adjacentState.getBlock() instanceof WindowBlock;
		}
	}

	private static final class LocometalLadder extends LadderBlock {
		private LocometalLadder(Properties properties) {
			super(properties);
		}
	}

	private static final class FlywheelBlock extends RotatedPillarBlock {
		private FlywheelBlock(Properties properties) {
			super(properties);
		}

		@Override
		public boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
			return adjacentState.getBlock() instanceof FlywheelBlock;
		}
	}

	private enum BoilerStyle implements net.minecraft.util.StringRepresentable {
		FLAT, GULLET, SMOKEBOX;

		@Override
		public String getSerializedName() {
			return name().toLowerCase(java.util.Locale.ROOT);
		}
	}
}