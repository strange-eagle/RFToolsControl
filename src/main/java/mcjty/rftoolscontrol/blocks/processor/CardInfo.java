package mcjty.rftoolscontrol.blocks.processor;

import mcjty.rftoolscontrol.logic.compiled.CompiledCard;
import mcjty.rftoolscontrol.logic.running.ProgException;
import net.minecraft.nbt.NBTTagCompound;

import static mcjty.rftoolscontrol.logic.running.ExceptionType.EXCEPT_NOINTERNALSLOT;

public class CardInfo {

    // 32-bit field for item allocation
    private int itemAllocation;
    // 32-bit field for variable allocation
    private int varAllocation;

    private CompiledCard compiledCard;

    private int slotCache[] = null;
    private int varCache[] = null;

    public int getItemAllocation() {
        return itemAllocation;
    }

    public void setItemAllocation(int itemAllocation) {
        this.itemAllocation = itemAllocation;
        slotCache = null;
    }

    public int getVarAllocation() {
        return varAllocation;
    }

    public void setVarAllocation(int varAllocation) {
        this.varAllocation = varAllocation;
        varCache = null;
    }

    public void setCompiledCard(CompiledCard compiledCard) {
        this.compiledCard = compiledCard;
    }

    public CompiledCard getCompiledCard() {
        return compiledCard;
    }

    public Integer getRealSlot(Integer virtualSlot) {
        if (virtualSlot == null) {
            return null;
        }
        return getRealSlot((int)virtualSlot);
    }

    public int getRealSlot(int virtualSlot) {
        if (slotCache == null) {
            slotCache = new int[ProcessorTileEntity.ITEM_SLOTS];
            int idx = 0;
            for (int i = 0 ; i < ProcessorTileEntity.ITEM_SLOTS ; i++) {
                if (((itemAllocation >> i) & 1) == 1) {
                    slotCache[idx] = i;
                    idx++;
                }
            }
            for ( ; idx < ProcessorTileEntity.ITEM_SLOTS ; idx++) {
                slotCache[idx] = -1;
            }
        }
        if (virtualSlot < 0 && virtualSlot >= ProcessorTileEntity.ITEM_SLOTS) {
            throw new ProgException(EXCEPT_NOINTERNALSLOT);
        }
        int realSlot = slotCache[virtualSlot];
        if (realSlot == -1) {
            throw new ProgException(EXCEPT_NOINTERNALSLOT);
        }

        return realSlot + ProcessorContainer.SLOT_BUFFER;
    }

    public int getRealVar(int virtualVar) {
        if (varCache == null) {
            varCache = new int[ProcessorTileEntity.MAXVARS];
            int idx = 0;
            for (int i = 0 ; i < ProcessorTileEntity.MAXVARS ; i++) {
                if (((varAllocation >> i) & 1) == 1) {
                    varCache[idx] = i;
                    idx++;
                }
            }
            for ( ; idx < ProcessorTileEntity.MAXVARS ; idx++) {
                varCache[idx] = -1;
            }
        }
        if (virtualVar < 0 && virtualVar >= ProcessorTileEntity.MAXVARS) {
            return -1;
        }
        return varCache[virtualVar];
    }

    public Integer getRealVar(Integer virtualVar) {
        if (virtualVar == null) {
            return null;
        }
        return getRealVar((int)virtualVar);
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("itemAlloc", itemAllocation);
        tag.setInteger("varAlloc", varAllocation);
        return tag;
    }

    public static CardInfo readFromNBT(NBTTagCompound tag) {
        CardInfo cardInfo = new CardInfo();
        cardInfo.itemAllocation = tag.getInteger("itemAlloc");
        cardInfo.varAllocation = tag.getInteger("varAlloc");
        cardInfo.slotCache = null;
        cardInfo.varCache = null;
        return cardInfo;
    }
}
