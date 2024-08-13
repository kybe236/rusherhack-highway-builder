package org.example;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.events.client.EventUpdate;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.NumberSetting;

import java.util.ArrayList;

/**
 * Example rusherhack module
 *
 * @author kybe236
 */
public class ExampleModule extends ToggleableModule {
	ArrayList<BlockPos> mine = new ArrayList<BlockPos>();
	ArrayList<BlockPos> place = new ArrayList<BlockPos>();
	
	/**
	 * Settings
	 */
	private final NumberSetting withr = new NumberSetting("With right", "with of the highway", 3, 1, 6)
			.incremental(1);
	private final NumberSetting withl = new NumberSetting("With left", "with of the highway", 3, 1, 6)
			.incremental(1);
	private final BooleanSetting guardRails = new BooleanSetting("Guard Rails", "Adds guard rails to the highway", true);
	
	/**
	 * Constructor
	 */
	public ExampleModule() {
		super("Highway Builder", "Builds highways", ModuleCategory.CLIENT);

		this.registerSettings(
				this.withl,
				this.withr,
				this.guardRails
		);
	}
	
	@Override
	public void onEnable() {
		if(mc.level != null) {
			ChatUtils.print("[HIGHWAY BUILDER] Enabled");
			mine.clear();
			place.clear();
			GetBlockPosToMine();
		}
	}
	
	@Override
	public void onDisable() {
		if(mc.level != null) {
			ChatUtils.print("[HIGHWAY BUILDER] Disabled");
			mine.clear();
			place.clear();
		}
	}

	@Subscribe
	public void onTick(EventUpdate event) {
		if (mc.level == null || mc.player == null || !mc.player.isAlive()) return;
		if (!this.isToggled()) return;
		if (mine.isEmpty()) {
			//GetBlockPosToMine();
		}
		if (place.isEmpty()) {
			GetBlockPosToPlace();
		}
		if (!mine.isEmpty()) {
			BlockPos block = mine.get(0);
			RusherHackAPI.getRotationManager().updateRotation(block);
			// add mining
			mine.remove(0);
		}
		if (!place.isEmpty()) {
			BlockPos block = place.get(0);
			RusherHackAPI.getRotationManager().updateRotation(block);
			RusherHackAPI.interactions().placeBlock(block, InteractionHand.MAIN_HAND, true);
			place.remove(0);
		}
	}

	private void GetBlockPosToMine() {
		// Get the player's position
		assert mc.player != null;
		BlockPos playerPos = mc.player.blockPosition();

		//blocks to the right
		for (int w = 0; w != (int) (this.withl.getValue()) + 1; w++) {
			for (int h = -1; h < 2; h++) {
				// Get the block to mine in front of the player with
				int[] direction = GetDirection();
				if (!mc.level.getBlockState(new BlockPos(playerPos.getX() + direction[0] * w, playerPos.getY() + h, playerPos.getZ() + direction[1] * w)).isAir()) {
					BlockPos block = new BlockPos(playerPos.getX() + direction[0] * w, playerPos.getY() + h, playerPos.getZ() + direction[1] * w);
					mine.add(block);
				}
			}
		}
		// blocks to the left
		for (int w = -1; w != -(int) (this.withr.getValue()); w--){
			for (int h = -1; h < 2; h++) {
				// Get the block to mine in front of the player with
				int[] direction = GetDirection();
				if (!mc.level.getBlockState(new BlockPos(playerPos.getX() + direction[0] * w, playerPos.getY() + h, playerPos.getZ() + direction[1] * w)).isAir()) {
					BlockPos block = new BlockPos(playerPos.getX() + direction[0] * w, playerPos.getY() + h, playerPos.getZ() + direction[1] * w);
					mine.add(block);
				}
			}
		}
	}

	private void GetBlockPosToPlace() {
		// Get the player's position
		assert mc.player != null;
		BlockPos playerPos = mc.player.blockPosition();

		// blocks to the right
		for (int w = 0; w < ((int) (this.withr.getValue()) + 1); w++) {
			// Get the block to place in front of the player with
			int[] direction = GetDirection();
			//check if block is air
			if (mc.level.getBlockState(new BlockPos(playerPos.getX() + direction[0] * w, playerPos.getY() - 1, playerPos.getZ() + direction[1] * w)).isAir()) {
				BlockPos block = new BlockPos(playerPos.getX() + direction[0] * w, playerPos.getY() - 1, playerPos.getZ() + direction[1] * w);
				place.add(block);
			}
		}
		//blocks to the left
		for (int w = -1; w > -(int) (this.withl.getValue()); w--) {
			// Get the block to place in front of the player with
			int[] direction = GetDirection();
			if (mc.level.getBlockState(new BlockPos(playerPos.getX() + direction[0] * w, playerPos.getY() - 1, playerPos.getZ() + direction[1] * w)).isAir()) {
				BlockPos block = new BlockPos(playerPos.getX() + direction[0] * w, playerPos.getY() - 1, playerPos.getZ() + direction[1] * w);
				place.add(block);
			}
		}
		//guard rales
		if (this.guardRails.getValue()) {
			// place rails at the end of withr
			int[] direction = GetDirection();
			if (mc.level.getBlockState(new BlockPos(playerPos.getX() + direction[0] * (int) (this.withr.getValue()), playerPos.getY() - 1, playerPos.getZ() + direction[1] * (int) (this.withr.getValue()))).isAir()) {
				BlockPos block = new BlockPos(playerPos.getX() + direction[0] * (int) (this.withr.getValue()), playerPos.getY() - 1, playerPos.getZ() + direction[1] * (int) (this.withr.getValue()));
				place.add(block);
			}
			// place rails at the end of withl
			if (mc.level.getBlockState(new BlockPos(playerPos.getX() + direction[0] * -(int) (this.withl.getValue()), playerPos.getY() - 1, playerPos.getZ() + direction[1] * -(int) (this.withl.getValue()))).isAir()) {
				BlockPos block = new BlockPos(playerPos.getX() + direction[0] * -(int) (this.withl.getValue()), playerPos.getY() - 1, playerPos.getZ() + direction[1] * -(int) (this.withl.getValue()));
				place.add(block);
			}
		}
	}

	private int[] GetDirection() {
		// Get the player's position
		assert mc.player != null;
		float yaw = mc.player.getYRot();

		// Get the direction the player is facing
		double x = Math.cos(Math.toRadians(yaw));
		double z = Math.sin(Math.toRadians(yaw));

		//return in wich straight direction the player is facing
		if (Math.abs(x) > Math.abs(z)) {
			if (x > 0) {
				return new int[]{1, 0};
			} else {
				return new int[]{-1, 0};
			}
		} else {
			if (z > 0) {
				return new int[]{0, 1};
			} else {
				return new int[]{0, -1};
			}
		}
	}
}
