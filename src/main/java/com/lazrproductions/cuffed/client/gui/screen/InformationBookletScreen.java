package com.lazrproductions.cuffed.client.gui.screen;

import com.lazrproductions.lazrslib.client.font.FontUtilities;
import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.lazrproductions.lazrslib.client.screen.base.BlitCoordinates;
import com.lazrproductions.lazrslib.client.screen.base.ScreenRect;
import com.lazrproductions.lazrslib.client.screen.base.ScreenTexture;
import com.lazrproductions.lazrslib.client.ui.Alignment;
import com.lazrproductions.lazrslib.client.ui.UIUtilities;
import com.lazrproductions.lazrslib.client.ui.element.AbstractElement;
import com.lazrproductions.lazrslib.client.ui.element.HorizontalElement;
import com.lazrproductions.lazrslib.client.ui.element.ItemIconElement;
import com.lazrproductions.lazrslib.client.ui.element.LinkElement;
import com.lazrproductions.lazrslib.client.ui.element.ScaledTextureElement;
import com.lazrproductions.lazrslib.client.ui.element.TextElement;
import com.lazrproductions.lazrslib.client.ui.element.TextureElement;
import com.lazrproductions.lazrslib.client.ui.element.VerticalElement;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.HashMap;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector2i;

@OnlyIn(Dist.CLIENT)
public class InformationBookletScreen extends GenericScreen {
   public static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath("cuffed", "textures/gui/information_booklet.png");
   private static final int FIXED_PAGE_HEIGHT = 202;
   private static final ScreenTexture FRONT_COVER_PAGE = new ScreenTexture(TEXTURE_LOCATION, 146.0F, 0.0F, 146, 180, 512, 512);
   private static final ScreenTexture BACK_COVER_PAGE = new ScreenTexture(TEXTURE_LOCATION, 0.0F, 0.0F, 146, 180, 512, 512);
   private static final ScreenTexture BASIC_LEFT_PAGE = new ScreenTexture(TEXTURE_LOCATION, 0.0F, 180.0F, 146, 180, 512, 512);
   private static final ScreenTexture BASIC_RIGHT_PAGE = new ScreenTexture(TEXTURE_LOCATION, 146.0F, 180.0F, 146, 180, 512, 512);
   private static final ScreenTexture RIGHT_BUTTON = new ScreenTexture(TEXTURE_LOCATION, 0.0F, 360.0F, 18, 10, 512, 512);
   private static final ScreenTexture RIGHT_BUTTON_HIGHLIGHTED = new ScreenTexture(TEXTURE_LOCATION, 18.0F, 360.0F, 18, 10, 512, 512);
   private static final ScreenTexture LEFT_BUTTON = new ScreenTexture(TEXTURE_LOCATION, 0.0F, 370.0F, 18, 10, 512, 512);
   private static final ScreenTexture LEFT_BUTTON_HIGHLIGHTED = new ScreenTexture(TEXTURE_LOCATION, 18.0F, 370.0F, 18, 10, 512, 512);
   private static final ScreenTexture BACK_BUTTON = new ScreenTexture(TEXTURE_LOCATION, 35.0F, 399.0F, 18, 10, 512, 512);
   private static final ScreenTexture BACK_BUTTON_HIGHLIGHTED = new ScreenTexture(TEXTURE_LOCATION, 53.0F, 399.0F, 18, 10, 512, 512);
   private static final ScreenTexture HOME_BUTTON = new ScreenTexture(TEXTURE_LOCATION, 0.0F, 381.0F, 11, 11, 512, 512);
   private static final ScreenTexture HOME_BUTTON_HIGHLIGHTED = new ScreenTexture(TEXTURE_LOCATION, 11.0F, 381.0F, 11, 11, 512, 512);
   static final int WRITING_COLOR = ARGB32.color(255, 148, 116, 90);
   static final int HIGHLIGHTED_COLOR = -16736257;
   public ArrayList<InformationBookletScreen.GenericPage> pages;
   HashMap<String, Integer> encyclopediaMap = new HashMap<>();
   int currentPage = 0;
   int previousPage = -1;
   int pageHeight;
   int pageWidth;

   public InformationBookletScreen(Minecraft instance) {
      super(instance);
   }

   @Override
   public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      this.pageHeight = 202;
      this.pageWidth = Mth.floor((float)this.pageHeight * 0.8111111F);
      if (this.minecraft != null) {
         this.drawContent(graphics, partialTick);
      }

      super.render(graphics, mouseX, mouseY, partialTick);
   }

   public void drawContent(@Nonnull GuiGraphics graphics, float partialTick) {
      this.assemblePages(this.minecraft);
      InformationBookletScreen.GenericPage page = this.pages.get(this.currentPage);
      this.renderBackground(graphics, this.mouseX, this.mouseY, partialTick);
      this.pages
         .get(this.currentPage)
         .renderPage(
            this.minecraft, graphics, partialTick, this.mouseX, this.mouseY, this.lastMouseInput.getAction() == 1 && this.lastMouseInput.getInput() == 0
         );
      if (this.pages.get(this.currentPage) instanceof InformationBookletScreen.DoublePage) {
         int l_pN = this.currentPage * 2 - 1;
         int r_pN = l_pN + 1;
         Component l_pageNumber = Component.literal(l_pN + "");
         int l_h = Mth.floor(9.0F);
         int l_w = Mth.floor((float)this.minecraft.font.width(l_pageNumber));
         int l_x = Mth.floor((float)(page.getPageRect().getFromX() + 26));
         int l_y = Mth.floor((float)(page.getPageRect().getToY() - l_h - 12));
         FontUtilities.drawText(this.minecraft, graphics, new BlitCoordinates(l_x, l_y, l_w, l_h), l_pageNumber, WRITING_COLOR, false);
         Component r_pageNumber = Component.literal(r_pN + "");
         int r_h = Mth.floor(9.0F);
         int r_w = Mth.floor((float)this.minecraft.font.width(l_pageNumber));
         int r_x = Mth.floor((float)(page.getPageRect().getToX() - r_w - 26));
         int r_y = Mth.floor((float)(page.getPageRect().getToY() - r_h - 12));
         FontUtilities.drawText(this.minecraft, graphics, new BlitCoordinates(r_x, r_y, r_w, r_h), r_pageNumber, WRITING_COLOR, false);
      }

      boolean pageWasChanged = false;
      if (this.currentPage > 1) {
         float aspectRatioOfTexture = 1.0F;
         int h = Mth.floor(10.0F);
         int w = Mth.floor((float)h * aspectRatioOfTexture);
         int x = Mth.floor((float)(page.getPageRect().getFromX() + this.pageWidth / 2 - Mth.floor(18.0F) / 2)) - 4 - w;
         int y = Mth.floor((float)(page.getPageRect().getToY() - h - 12));
         if (drawButton(
            graphics,
            new BlitCoordinates(x, y, w, h),
            HOME_BUTTON,
            HOME_BUTTON_HIGHLIGHTED,
            (double)this.mouseX,
            (double)this.mouseY,
            this.lastMouseInput.getAction() == 1 && this.lastMouseInput.getInput() == 0
         )) {
            this.setPage(this.minecraft, 1);
            pageWasChanged = true;
         }
      }

      if (this.previousPage > -1) {
         float aspectRatioOfTexture = 1.8F;
         int h = Mth.floor(10.0F);
         int w = Mth.floor((float)h * aspectRatioOfTexture);
         int x = Mth.floor((float)(page.getPageRect().getFromX() + 18));
         int y = Mth.floor((float)(page.getPageRect().getFromY() + 14));
         if (drawButton(
            graphics,
            new BlitCoordinates(x, y, w, h),
            BACK_BUTTON,
            BACK_BUTTON_HIGHLIGHTED,
            (double)this.mouseX,
            (double)this.mouseY,
            this.lastMouseInput.getAction() == 1 && this.lastMouseInput.getInput() == 0
         )) {
            this.setPage(this.minecraft, this.previousPage);
            this.previousPage = -1;
            pageWasChanged = true;
         }
      }

      if (this.currentPage > 0) {
         float aspectRatioOfTexture = 1.8F;
         int h = Mth.floor(10.0F);
         int w = Mth.floor((float)h * aspectRatioOfTexture);
         int x = Mth.floor((float)(page.getPageRect().getFromX() + this.pageWidth / 2 - w / 2));
         int y = Mth.floor((float)(page.getPageRect().getToY() - h - 12));
         if (drawButton(
            graphics,
            new BlitCoordinates(x, y, w, h),
            LEFT_BUTTON,
            LEFT_BUTTON_HIGHLIGHTED,
            (double)this.mouseX,
            (double)this.mouseY,
            this.lastMouseInput.getAction() == 1 && this.lastMouseInput.getInput() == 0
         )) {
            this.currentPage--;
            this.previousPage = -1;
            pageWasChanged = true;
            this.minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN);
         }
      }

      if (!pageWasChanged && this.currentPage + 1 < this.pages.size()) {
         float aspectRatioOfTexture = 1.8F;
         int h = Mth.floor(10.0F);
         int w = Mth.floor((float)h * aspectRatioOfTexture);
         int x = Mth.floor((float)(page.getPageRect().getToX() - this.pageWidth / 2 - w / 2));
         int y = Mth.floor((float)(page.getPageRect().getToY() - h - 12));
         if (drawButton(
            graphics,
            new BlitCoordinates(x, y, w, h),
            RIGHT_BUTTON,
            RIGHT_BUTTON_HIGHLIGHTED,
            (double)this.mouseX,
            (double)this.mouseY,
            this.lastMouseInput.getAction() == 1 && this.lastMouseInput.getInput() == 0
         )) {
            this.currentPage++;
            this.previousPage = -1;
            this.minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN);
         }
      }
   }

   public void setPage(@Nonnull Minecraft insance, int value) {
      this.previousPage = this.currentPage;
      this.currentPage = value;
      insance.player.playSound(SoundEvents.BOOK_PAGE_TURN);
   }

   public void assemblePages(@Nonnull Minecraft instance) {
      this.pageHeight = 202;
      this.pageWidth = Mth.floor((float)this.pageHeight * 0.8111111F);
      int contentWidth = this.pageWidth - 24 - 24;
      this.pages = new ArrayList<>();
      this.pages.add(new InformationBookletScreen.CoverPage(FRONT_COVER_PAGE));
      this.pages
         .add(
            new InformationBookletScreen.DoublePage(
               new InformationBookletScreen.ComplexPage(
                  BASIC_LEFT_PAGE,
                  new VerticalElement(
                     BlitCoordinates.DEFAULT,
                     2,
                     new HorizontalElement[]{
                        new HorizontalElement(
                           new AbstractElement[]{
                              new TextureElement(
                                 instance, new ScreenTexture(TEXTURE_LOCATION, 438.0F, 253.0F, 65, 23, 512, 512), Alignment.CENTER, Mth.floor(27.756F)
                              )
                           }
                        ),
                        new HorizontalElement(
                           new AbstractElement[]{
                              new ScaledTextureElement(
                                 instance, InformationBookletScreen.GenericPage.SEPARATOR_TEXTURE, Mth.floor((float)contentWidth * 0.09615385F)
                              )
                           }
                        ),
                        new HorizontalElement(
                           new AbstractElement[]{
                              new TextElement(instance, contentWidth, Component.translatable("guide.cuffed.title_page.info"), WRITING_COLOR, false)
                           }
                        )
                     }
                  )
               ),
               new InformationBookletScreen.ComplexPage(
                  BASIC_RIGHT_PAGE,
                  new VerticalElement(
                     BlitCoordinates.DEFAULT,
                     2,
                     new HorizontalElement[]{
                        new HorizontalElement(
                           new AbstractElement[]{
                              new TextElement(instance, contentWidth, Component.literal("Warden's guide has been discontinued, use the"), WRITING_COLOR, false)
                           }
                        ),
                        new HorizontalElement(
                           new AbstractElement[]{new LinkElement(instance, contentWidth, Component.literal("Official Cuffed Wiki"), Alignment.CENTER, () -> {
                           }, WRITING_COLOR, -16736257)}
                        ),
                        new HorizontalElement(
                           new AbstractElement[]{
                              new TextElement(instance, contentWidth, Component.literal("to find information about the mod."), WRITING_COLOR, false)
                           }
                        )
                     }
                  )
               )
            )
         );
      this.pages.add(new InformationBookletScreen.CoverPage(BACK_COVER_PAGE));
   }

   public int getItemEncyclopediaPage(Item item) {
      return this.getItemEncyclopediaPage(new ItemStack(item));
   }

   public int getItemEncyclopediaPage(ItemStack stack) {
      return this.getItemEncyclopediaPage(stack.getDescriptionId());
   }

   public int getItemEncyclopediaPage(String path) {
      return this.encyclopediaMap.containsKey(path) ? this.encyclopediaMap.get(path) : 0;
   }

   public InformationBookletScreen.DoublePage createItemEncyclopediaPage(int wouldBeIndex, @Nonnull Minecraft instance, Item item, int contentWidth) {
      return this.createItemEncyclopediaPage(wouldBeIndex, instance, new ItemStack(item), contentWidth);
   }

   public InformationBookletScreen.DoublePage createItemEncyclopediaPage(int wouldBeIndex, @Nonnull Minecraft instance, ItemStack stack, int contentWidth) {
      return this.createItemEncyclopediaPage(wouldBeIndex, instance, stack.getItem().getDescriptionId(), stack, contentWidth);
   }

   public InformationBookletScreen.DoublePage createItemEncyclopediaPage(
      int wouldBeIndex, @Nonnull Minecraft instance, String path, ItemStack stack, int contentWidth
   ) {
      if (!this.encyclopediaMap.containsKey(path)) {
         this.encyclopediaMap.put(path, wouldBeIndex);
      } else {
         this.encyclopediaMap.replace(path, wouldBeIndex);
      }

      return new InformationBookletScreen.DoublePage(
         new InformationBookletScreen.ComplexPage(
            BASIC_LEFT_PAGE,
            new VerticalElement(
               BlitCoordinates.DEFAULT,
               new HorizontalElement[]{
                  new HorizontalElement(
                     new AbstractElement[]{
                        new TextElement(instance, contentWidth - 36, Component.translatable(path).withStyle(ChatFormatting.BOLD), WRITING_COLOR, false),
                        new ItemIconElement(instance, stack, Alignment.CENTER_RIGHT, 36)
                     }
                  ),
                  new HorizontalElement(
                     new AbstractElement[]{new TextElement(instance, contentWidth, Component.translatable(path + ".desc"), WRITING_COLOR, false)}
                  )
               }
            )
         ),
         new InformationBookletScreen.BlankPage(BASIC_RIGHT_PAGE)
      );
   }

   public static boolean drawButton(
      GuiGraphics graphics, BlitCoordinates pos, ScreenTexture texture, ScreenTexture highlightedTexture, double mouseX, double mouseY, boolean mouseDown
   ) {
      ScreenRect area = pos.toRect();
      if (area.positionEnvlopes(mouseX, mouseY)) {
         ScreenUtilities.drawTexture(graphics, pos, highlightedTexture);
         if (mouseDown) {
            return true;
         }
      } else {
         ScreenUtilities.drawTexture(graphics, pos, texture);
      }

      return false;
   }

   static class BlankPage extends InformationBookletScreen.GenericPage {
      public BlankPage(ScreenTexture background) {
         super(background);
      }

      @Override
      protected void renderContent(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY, boolean mouseDown) {
         BlitCoordinates pos = new BlitCoordinates(
            this.getContentBlitCoords().toRect().getCenter().x() - 18, this.getContentBlitCoords().toRect().getCenter().y() - 18, 36, 36
         );
         ScreenUtilities.drawTexture(graphics, pos, new ScreenTexture(InformationBookletScreen.TEXTURE_LOCATION, 292.0F, 0.0F, 35, 35, 512, 512));
      }
   }

   static class ComplexPage extends InformationBookletScreen.GenericPage {
      final VerticalElement element;

      public ComplexPage(ScreenTexture background, VerticalElement element) {
         super(background);
         this.element = element;
      }

      @Override
      protected void renderContent(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY, boolean mouseDown) {
         this.element.setAvailableArea(this.getContentBlitCoords());
         UIUtilities.drawPage(instance, graphics, mouseX, mouseY, mouseDown, this.element);
      }
   }

   static class CoverPage extends InformationBookletScreen.GenericPage {
      public CoverPage(ScreenTexture background) {
         super(background);
      }

      @Override
      protected void renderContent(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY, boolean mouseDown) {
      }
   }

   static class DoublePage extends InformationBookletScreen.GenericPage {
      final InformationBookletScreen.GenericPage leftPage;
      final InformationBookletScreen.GenericPage rightPage;

      public DoublePage(InformationBookletScreen.GenericPage leftPage, InformationBookletScreen.GenericPage rightPage) {
         super(leftPage.backgroundtexture);
         this.leftPage = leftPage;
         this.leftPage.pageHorizontalOffset = -leftPage.getPageRect().getWidth() / 2;
         this.rightPage = rightPage;
         this.rightPage.pageHorizontalOffset = rightPage.getPageRect().getWidth() / 2;
      }

      @Override
      public void renderPage(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY, boolean mouseDown) {
         super.renderPage(instance, graphics, partialTick, mouseX, mouseY, mouseDown);
         this.leftPage.renderPage(instance, graphics, partialTick, mouseX, mouseY, mouseDown);
         this.rightPage.renderPage(instance, graphics, partialTick, mouseX, mouseY, mouseDown);
      }

      @Override
      protected void renderBackground(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY, boolean mouseDown) {
      }

      @Override
      protected void renderContent(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY, boolean mouseDown) {
      }

      @Override
      public ScreenRect getPageRect() {
         return new ScreenRect(
            this.leftPage.getPageRect().getFromX(),
            this.leftPage.getPageRect().getFromY(),
            this.rightPage.getPageRect().getToX(),
            this.rightPage.getPageRect().getToY()
         );
      }
   }

   abstract static class GenericPage {
      static final float PAGE_VERTICAL_PERCENTAGE = 0.8F;
      static final int CONTENT_PADDING_LEFT = 24;
      static final int CONTENT_PADDING_RIGHT = 24;
      static final int CONTENT_PADDING_TOP = 18;
      static final int CONTENT_PADDING_BOTTOM = 28;
      static final int HEADER_HEIGHT = 36;
      static final int ICON_HEIGHT = 36;
      static final int ICON_WIDTH = 36;
      static int SEPARATOR_HEIGHT = 0;
      static final ScreenTexture SEPARATOR_TEXTURE = new ScreenTexture(InformationBookletScreen.TEXTURE_LOCATION, 0.0F, 411.0F, 104, 9, 512, 512);
      ResourceLocation PAGE_LOCATION;
      private int screenWidth;
      private int screenHeight;
      private ScreenRect pageRect;
      private ScreenRect contentRect;
      private ScreenTexture backgroundtexture;
      public int pageHorizontalOffset;

      public GenericPage(ScreenTexture background) {
         this.backgroundtexture = background;
         this.calibratePage(Minecraft.getInstance());
      }

      public void renderPage(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY, boolean mouseDown) {
         this.calibratePage(instance);
         this.renderBackground(instance, graphics, partialTick, mouseX, mouseY, mouseDown);
         this.renderContent(instance, graphics, partialTick, mouseX, mouseY, mouseDown);
      }

      protected void renderBackground(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY, boolean mouseDown) {
         ScreenUtilities.drawTexture(graphics, this.getPageBlitCoords(), this.backgroundtexture);
      }

      protected abstract void renderContent(@Nonnull Minecraft var1, @Nonnull GuiGraphics var2, float var3, int var4, int var5, boolean var6);

      private final void calibratePage(@Nonnull Minecraft instance) {
         Window window = Minecraft.getInstance().getWindow();
         this.screenWidth = window.getGuiScaledWidth();
         this.screenHeight = window.getGuiScaledHeight();
         int centerScreenX = this.screenWidth / 2;
         int centerScreenY = this.screenHeight / 2;
         float aspectRatioOfTexture = (float)this.backgroundtexture.getBoundsX() / (float)this.backgroundtexture.getBoundsY();
         int h = 202;
         int w = Mth.floor((float)h * aspectRatioOfTexture);
         int x = Mth.floor((float)(centerScreenX - w / 2)) + this.pageHorizontalOffset;
         int y = Mth.floor((float)(centerScreenY - h / 2));
         this.pageRect = new BlitCoordinates(x, y, w, h).toRect();
         this.contentRect = new ScreenRect(
            this.pageRect.getFromX() + 24, this.pageRect.getFromY() + 18, this.pageRect.getToX() - 24, this.pageRect.getToY() - 28
         );
         SEPARATOR_HEIGHT = Mth.floor((float)this.contentRect.getWidth() / 11.555555F);
      }

      public Vector2i getTopLeft() {
         return this.pageRect.getTopLeft();
      }

      public Vector2i getTopRight() {
         return this.pageRect.getTopRight();
      }

      public Vector2i getBottomLeft() {
         return this.pageRect.getBottomLeft();
      }

      public Vector2i getBottomRight() {
         return this.pageRect.getBottomRight();
      }

      public Vector2i getCenter() {
         return this.pageRect.getCenter();
      }

      public ScreenRect getContentRect() {
         return this.contentRect;
      }

      public BlitCoordinates getContentBlitCoords() {
         return this.getContentRect().toBlitCoordinates();
      }

      public ScreenRect getPageRect() {
         return this.pageRect;
      }

      public BlitCoordinates getPageBlitCoords() {
         return this.getPageRect().toBlitCoordinates();
      }
   }
}
