package com.teamcqr.chocolatequestrepoured.objects.blocks;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.client.gui.GUIExporter;
import com.teamcqr.chocolatequestrepoured.init.base.BlockBase;
import com.teamcqr.chocolatequestrepoured.tileentity.TileEntityExporter;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockExporter extends BlockBase implements ITileEntityProvider 
{
	public BlockExporter(String name, Material material) 
	{
		super(name, material);
		
		setBlockUnbreakable();
		setCreativeTab(CQRMain.CQRExporterChestTab);
		setSoundType(SoundType.ANVIL);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) 
	{
		playerIn.openGui(CQRMain.INSTANCE, GUIExporter.GUIID, worldIn, pos.getX(), pos.getY(), pos.getZ());
		//return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
		return true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityExporter();
	}
	
	public TileEntityExporter getTileEntity(IBlockAccess world, BlockPos pos) 
	{
		return (TileEntityExporter)world.getTileEntity(pos);
	}
}