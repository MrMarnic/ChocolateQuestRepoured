package com.teamcqr.chocolatequestrepoured.structurefile;

import com.teamcqr.chocolatequestrepoured.util.NBTUtil;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

class BannerInfo {
	
	private BlockPos position;
	
	public BannerInfo(BlockPos location) {
		this.position = location;
	}
	
	public NBTTagCompound getAsNBTTag() {
		NBTTagCompound tag = new NBTTagCompound();
		
		tag.setString("type", "bannerPos");
		
		NBTTagCompound posTag = NBTUtil.BlockPosToNBTTag(this.position);
		
		tag.setTag("position", posTag);
		
		
		return tag;
	}
	
	public BannerInfo(NBTTagCompound nbtTag) {
		if(nbtTag.getString("type").equalsIgnoreCase("bannerPos")) {
			
			NBTTagCompound posTag = nbtTag.getCompoundTag("position");
			int x = posTag.getInteger("x");
			int y = posTag.getInteger("y");
			int z = posTag.getInteger("z");
			
			this.position = new BlockPos(x, y, z);
		}
	}

	public BlockPos getPos() {
		return this.position;
	}

}
