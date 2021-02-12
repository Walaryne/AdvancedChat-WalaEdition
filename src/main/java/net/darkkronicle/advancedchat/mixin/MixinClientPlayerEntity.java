package net.darkkronicle.advancedchat.mixin;


import net.darkkronicle.advancedchat.AdvancedChat;
import net.darkkronicle.advancedchat.gui.tabs.AbstractChatTab;
import net.darkkronicle.advancedchat.gui.tabs.CustomChatTab;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) {
        AbstractChatTab tab = AdvancedChat.getAdvancedChatHud().getCurrentTab();
        String newMessage = message;

        if (tab instanceof CustomChatTab) {
            CustomChatTab customTab = (CustomChatTab) tab;
            if (!customTab.getStartingMessage().isEmpty() && !message.startsWith("/")) {
                newMessage = customTab.getStartingMessage() + message;
            }
        }

        ((ClientPlayerEntity) (Object) this).networkHandler.sendPacket(new ChatMessageC2SPacket(newMessage));
        ci.cancel();
    }

}
