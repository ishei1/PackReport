package com.example;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.loader.api.FabricLoader;

import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class ExampleMod implements ModInitializer {
	public static final String MOD_ID = "packreport";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	public static String goonscript = "";

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		StringBuilder report = new StringBuilder();
		LOGGER.info("Hello Fabric world!");
		LOGGER.info("Found {} mods",FabricLoader.getInstance().getAllMods().size());
		report.append(" PackReport: ")
		      .append("\n");
		report.append("---------------------------")
		      .append("\n");
		report.append(" Number of Mods: ")
		      .append(FabricLoader.getInstance().getAllMods().size())
		      .append("\n");
		report.append(" Java Version: ")
		      .append(System.getProperty("java.version"))
		      .append("\n");
		report.append(" Minecraft Version ")
		      .append(FabricLoader.getInstance().getRawGameVersion())
		      .append("\n");
		report.append("---------------------------")
                      .append("\n");
	
		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			ModMetadata MarkiniZucherberginisData=mod.getMetadata();
			LOGGER.info("Name : {} | ID: {} | Version : {}",MarkiniZucherberginisData.getName(),MarkiniZucherberginisData.getId(),MarkiniZucherberginisData.getVersion());
			String datawaste=MarkiniZucherberginisData.getDescription();
			
			report.append(" Name: ")
			      .append(MarkiniZucherberginisData.getName())
			      .append(" | ID: ")
			      .append(MarkiniZucherberginisData.getId())
			      .append(" | Version: ")
			      .append(MarkiniZucherberginisData.getVersion());
			if (datawaste.isEmpty()) {
                                report.append(" | Description : no description ");
                        }
                        else {
                                report.append(" | Description: ")
				      .append(datawaste);
			}
			report.append("\n");
		}
		LOGGER.info(" report: {} ",report.toString());
		goonscript = report.toString();
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
