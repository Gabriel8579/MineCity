package br.com.gamemods.minecity.forge.base.accessors.item;

import br.com.gamemods.minecity.api.permission.PermissionFlag;
import br.com.gamemods.minecity.api.world.BlockPos;
import br.com.gamemods.minecity.api.world.Direction;
import br.com.gamemods.minecity.forge.base.accessors.block.IBlock;
import br.com.gamemods.minecity.forge.base.accessors.block.IBlockSnapshot;
import br.com.gamemods.minecity.forge.base.accessors.block.IState;
import br.com.gamemods.minecity.forge.base.accessors.entity.base.IEntityPlayerMP;
import br.com.gamemods.minecity.forge.base.accessors.world.IWorldServer;
import br.com.gamemods.minecity.forge.base.core.Referenced;
import br.com.gamemods.minecity.forge.base.core.transformer.forge.ForgeInterfaceTransformer;
import br.com.gamemods.minecity.reactive.reaction.NoReaction;
import br.com.gamemods.minecity.reactive.reaction.Reaction;
import br.com.gamemods.minecity.reactive.reaction.SingleBlockReaction;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockTallGrass;

@Referenced(at = ForgeInterfaceTransformer.class)
public interface IItemDye extends IItem
{
    @Override
    default Reaction reactRightClickBlock(IEntityPlayerMP player, IItemStack stack, boolean offHand, IState state, BlockPos pos, Direction face)
    {
        int meta = stack.getMeta();
        if(meta == 15)
        {
            IBlock block = state.getIBlock();
            if(block instanceof BlockCrops)
                return new SingleBlockReaction(pos, PermissionFlag.HARVEST);
            else if(block instanceof BlockTallGrass &&
                    !(pos.world.getInstance(IWorldServer.class).getIBlock(pos.x, pos.y-1, pos.z) instanceof BlockTallGrass))
                return new SingleBlockReaction(pos.add(Direction.UP), PermissionFlag.MODIFY);
            else
                return NoReaction.INSTANCE;
        }

        if(meta != 3)
            return NoReaction.INSTANCE;

        if(!(state.getIBlock() instanceof BlockOldLog))
            return NoReaction.INSTANCE;

        if(state.getEnumOrdinalOrMeta("variant", m-> m&3) != 3)
            return NoReaction.INSTANCE;

        return new SingleBlockReaction(pos.add(face.getOpposite()), PermissionFlag.MODIFY);
    }

    @Override
    default Reaction reactBlockPlace(IEntityPlayerMP player, IItemStack stack, boolean offHand,
                                     IBlockSnapshot snap)
    {
        BlockPos pos = snap.getPosition(player.getServer());
        if(stack.getMeta() != 15)
            return new SingleBlockReaction(pos, PermissionFlag.MODIFY);

        return NoReaction.INSTANCE;
    }

    @Override
    default boolean isHarvest(IItemStack stack)
    {
        int meta = stack.getMeta();
        return meta == 3 || meta == 15;
    }
}
