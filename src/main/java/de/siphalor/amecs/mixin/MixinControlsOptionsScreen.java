package de.siphalor.amecs.mixin;

import de.siphalor.amecs.api.KeyModifiers;
import de.siphalor.amecs.api.Utils;
import de.siphalor.amecs.util.IKeyBinding;
import net.minecraft.client.gui.screen.controls.ControlsOptionsScreen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.SystemUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("WeakerAccess")
@Mixin(ControlsOptionsScreen.class)
public class MixinControlsOptionsScreen {
	@Shadow public KeyBinding focusedBinding;

	@Shadow public long time;

	@Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/GameOptions;setKeyCode(Lnet/minecraft/client/options/KeyBinding;Lnet/minecraft/client/util/InputUtil$KeyCode;)V", ordinal = 0))
	public void clearKeyBinding(int keyCode, int scanCode, int int_3, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		((IKeyBinding) focusedBinding).amecs$getKeyModifiers().unset();
	}

	@Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/GameOptions;setKeyCode(Lnet/minecraft/client/options/KeyBinding;Lnet/minecraft/client/util/InputUtil$KeyCode;)V", ordinal = 1), cancellable = true)
	public void onKeyPressed(int keyCode, int scanCode, int int_3, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(true);
		if(focusedBinding.isNotBound()) {
			focusedBinding.setKeyCode(InputUtil.getKeyCode(keyCode, scanCode));
		} else {
			int mainKeyCode = ((IKeyBinding) focusedBinding).amecs$getKeyCode().getKeyCode();
			KeyModifiers keyModifiers = ((IKeyBinding) focusedBinding).amecs$getKeyModifiers();
			if (Utils.isModifier(mainKeyCode) && !Utils.isModifier(keyCode)) {
				focusedBinding.setKeyCode(InputUtil.getKeyCode(keyCode, scanCode));
				if (Utils.isShiftKey(mainKeyCode)) keyModifiers.setShift(true);
				if (Utils.isControlKey(mainKeyCode)) keyModifiers.setControl(true);
				if (Utils.isAltKey(mainKeyCode)) keyModifiers.setAlt(true);
			} else {
				if (Utils.isShiftKey(keyCode)) keyModifiers.setShift(true);
				if (Utils.isControlKey(keyCode)) keyModifiers.setControl(true);
				if (Utils.isAltKey(keyCode)) keyModifiers.setAlt(true);
				keyModifiers.cleanup(focusedBinding);
			}
		}
		time = SystemUtil.getMeasuringTimeMs();
		KeyBinding.updateKeysByCode();
	}
}
