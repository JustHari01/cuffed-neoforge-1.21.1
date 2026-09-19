package com.lazrproductions.cuffed.command;

import com.lazrproductions.cuffed.init.ModMenuTypes;
import com.lazrproductions.cuffed.inventory.FriskingMenu;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import javax.annotation.Nonnull;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DemoLupaCommand {
   public DemoLupaCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("demolupa").requires(src -> src.hasPermission(2) || !src.isPlayer()))
            .executes(this::executeDemoOpen)
      );
   }

   private static Container createDemoContainer() {
      final NonNullList<ItemStack> items = NonNullList.withSize(54, ItemStack.EMPTY);
      items.set(0, new ItemStack(Items.DIAMOND_HELMET));
      items.set(1, new ItemStack(Items.DIAMOND_CHESTPLATE));
      items.set(2, new ItemStack(Items.DIAMOND_LEGGINGS));
      items.set(3, new ItemStack(Items.DIAMOND_BOOTS));
      items.set(8, new ItemStack(Items.SHIELD));
      items.set(9, new ItemStack(Items.DIAMOND, 64));
      items.set(10, new ItemStack(Items.GOLD_INGOT, 32));
      items.set(11, new ItemStack(Items.IRON_INGOT, 64));
      items.set(12, new ItemStack(Items.EMERALD, 16));
      items.set(13, new ItemStack(Items.COOKED_BEEF, 32));
      items.set(14, new ItemStack(Items.GOLDEN_APPLE, 8));
      items.set(15, new ItemStack(Items.ENDER_PEARL, 16));
      items.set(16, new ItemStack(Items.TNT, 8));
      items.set(17, new ItemStack(Items.EXPERIENCE_BOTTLE, 32));
      items.set(18, new ItemStack(Items.NETHERITE_INGOT, 4));
      items.set(19, new ItemStack(Items.TOTEM_OF_UNDYING, 2));
      items.set(20, new ItemStack(Items.ENCHANTED_BOOK));
      items.set(21, new ItemStack(Items.NETHER_STAR));
      items.set(22, new ItemStack(Items.ELYTRA));
      items.set(23, new ItemStack(Items.WITHER_SKELETON_SKULL));
      items.set(24, new ItemStack(Items.DRAGON_EGG));
      items.set(25, new ItemStack(Items.HEART_OF_THE_SEA));
      items.set(26, new ItemStack(Items.TRIDENT));
      items.set(27, new ItemStack(Items.CROSSBOW));
      items.set(28, new ItemStack(Items.MUSIC_DISC_PIGSTEP));
      items.set(29, new ItemStack(Items.BLAZE_POWDER, 16));
      items.set(30, new ItemStack(Items.OBSIDIAN, 32));
      items.set(31, new ItemStack(Items.BEACON));
      items.set(32, new ItemStack(Items.AMETHYST_SHARD, 16));
      items.set(33, new ItemStack(Items.ECHO_SHARD, 4));
      items.set(34, new ItemStack(Items.SNIFFER_EGG));
      items.set(35, new ItemStack(Items.SUSPICIOUS_STEW));
      items.set(36, new ItemStack(Items.DIAMOND_SWORD));
      items.set(37, new ItemStack(Items.BOW));
      items.set(38, new ItemStack(Items.ARROW, 64));
      items.set(39, new ItemStack(Items.DIAMOND_PICKAXE));
      items.set(40, new ItemStack(Items.FLINT_AND_STEEL));
      items.set(41, new ItemStack(Items.ENDER_CHEST));
      items.set(42, new ItemStack(Items.COMPASS));
      items.set(43, new ItemStack(Items.SPYGLASS));
      items.set(44, new ItemStack(Items.CLOCK));
      return new Container() {
         public int getContainerSize() {
            return 54;
         }

         public boolean isEmpty() {
            for (ItemStack s : items) {
               if (!s.isEmpty()) {
                  return false;
               }
            }

            return true;
         }

         public ItemStack getItem(int index) {
            return index >= 0 && index < 54 ? (ItemStack)items.get(index) : ItemStack.EMPTY;
         }

         public ItemStack removeItem(int index, int count) {
            if (index >= 0 && index < 54) {
               ItemStack stack = (ItemStack)items.get(index);
               if (stack.isEmpty()) {
                  return ItemStack.EMPTY;
               } else {
                  ItemStack removed;
                  if (stack.getCount() <= count) {
                     items.set(index, ItemStack.EMPTY);
                     removed = stack;
                  } else {
                     removed = stack.split(count);
                  }

                  return removed;
               }
            } else {
               return ItemStack.EMPTY;
            }
         }

         public ItemStack removeItemNoUpdate(int index) {
            if (index >= 0 && index < 54) {
               ItemStack old = (ItemStack)items.get(index);
               items.set(index, ItemStack.EMPTY);
               return old;
            } else {
               return ItemStack.EMPTY;
            }
         }

         public void setItem(int index, @Nonnull ItemStack stack) {
            if (index >= 0 && index < 54) {
               items.set(index, stack);
            }
         }

         public void setChanged() {
         }

         public boolean stillValid(@Nonnull Player player) {
            return true;
         }

         public void clearContent() {
            for (int i = 0; i < 54; i++) {
               items.set(i, ItemStack.EMPTY);
            }
         }

         public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
            return index < 4 || index >= 8;
         }
      };
   }

   private int executeDemoOpen(CommandContext<CommandSourceStack> ctx) {
      final ServerPlayer sender = ((CommandSourceStack)ctx.getSource()).getPlayer();
      if (sender == null) {
         ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("Команда должна быть выполнена игроком."));
         return 0;
      } else {
         final Container demo = createDemoContainer();
         sender.openMenu(new MenuProvider() {
            public Component getDisplayName() {
               return Component.literal("Demo: Лупа");
            }

            public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
               FriskingMenu menu = new FriskingMenu((MenuType<?>)ModMenuTypes.FRISKING_MENU.get(), id, playerInventory, sender.getId(), demo, 5);
               menu.isLupaMenu.set(1);
               return menu;
            }
         });
         sender.sendSystemMessage(
            Component.literal(
               "§e[Demo Лупа] §fОткрыт демо-инвентарь с тестовыми предметами.\n§7• Взять предмет: §fзажми ЛКМ §71 сек → прогресс §e*|||||*\n§7• Дать предмет: §fперетащи из своего инвентаря мгновенно\n§7• Предмет в руке: §fзаблокирован (не взять пока держат)"
            )
         );
         return 1;
      }
   }
}
