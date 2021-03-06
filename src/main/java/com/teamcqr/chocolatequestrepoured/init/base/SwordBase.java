package com.teamcqr.chocolatequestrepoured.init.base;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.init.ModItems;
import com.teamcqr.chocolatequestrepoured.util.IHasModel;

import net.minecraft.item.ItemSword;

public class SwordBase extends ItemSword implements IHasModel
{
	public SwordBase(String name, ToolMaterial material) 
	{
		super(material);
		
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CQRMain.CQRItemsTab);
		
		ModItems.ITEMS.add(this);
	}
		
	@Override
	public void registerModels()
	{
		CQRMain.proxy.registerItemRenderer(this, 0,"inventory");
	}
}