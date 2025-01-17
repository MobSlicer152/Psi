/*
 * This class is distributed as part of the Psi Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package vazkii.psi.client.fx;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

// https://github.com/Vazkii/Botania/blob/1.15/src/main/java/vazkii/botania/client/fx/FXWisp.java
public class FXWisp extends TextureSheetParticle {

	private final float moteParticleScale;
	private final int moteHalfLife;

	public FXWisp(ClientLevel world, double d, double d1, double d2, double xSpeed, double ySpeed, double zSpeed,
			float size, float red, float green, float blue, float maxAgeMul) {
		super(world, d, d1, d2, 0, 0, 0);
		// super applies wiggle to motion so set it here instead
		xd = xSpeed;
		yd = ySpeed;
		zd = zSpeed;
		rCol = red;
		gCol = green;
		bCol = blue;
		alpha = 0.375F;
		gravity = 0;
		quadSize = (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F * size;
		moteParticleScale = quadSize;
		lifetime = (int) (28D / (Math.random() * 0.3D + 0.7D) * maxAgeMul);

		moteHalfLife = lifetime / 2;
		setSize(0.01F, 0.01F);

		xo = x;
		yo = y;
		zo = z;
		this.hasPhysics = true;
	}

	@Override
	public float getQuadSize(float scaleFactor) {
		float agescale = (float) age / (float) moteHalfLife;
		if (agescale > 1F) {
			agescale = 2 - agescale;
		}

		quadSize = moteParticleScale * agescale * 0.5F;
		return quadSize;
	}

	@Override
	protected int getLightColor(float partialTicks) {
		return 0xF000F0;
	}

	@Nonnull
	@Override
	public ParticleRenderType getRenderType() {
		return NORMAL_RENDER;
	}

	// [VanillaCopy] of super, without drag when onGround is true
	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime) {
			this.remove();
		}

		this.yd -= 0.04D * (double) this.gravity;
		this.move(this.xd, this.yd, this.zd);
		this.xd *= 0.9800000190734863D;
		this.yd *= 0.9800000190734863D;
		this.zd *= 0.9800000190734863D;
	}

	public void setGravity(float value) {
		gravity = value;
	}

	private static void beginRenderCommon(BufferBuilder bufferBuilder, TextureManager textureManager) {
		Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
		AbstractTexture tex = textureManager.getTexture(TextureAtlas.LOCATION_PARTICLES);
		tex.setFilter(true, false);
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
	}

	private static void endRenderCommon() {
		Minecraft.getInstance().textureManager.getTexture(TextureAtlas.LOCATION_PARTICLES).restoreLastBlurMipmap();
		//RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
	}

	private static final ParticleRenderType NORMAL_RENDER = new ParticleRenderType() {
		@Override
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			beginRenderCommon(bufferBuilder, textureManager);
		}

		@Override
		public void end(Tesselator tessellator) {
			tessellator.end();
			endRenderCommon();
		}

		@Override
		public String toString() {
			return "botania:wisp";
		}
	};

}
