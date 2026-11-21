package com.baykiebacon.nmtr_locometal.client;

import net.fabricmc.api.ClientModInitializer;

import com.baykiebacon.nmtr_locometal.ModItems;

public class NmtrLocometalClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		LocometalConnectedTextures.register(ModItems.registeredBlocks());
	}
}