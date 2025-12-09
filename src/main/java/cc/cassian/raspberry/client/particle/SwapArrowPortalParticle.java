package cc.cassian.raspberry.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SwapArrowPortalParticle extends PortalParticle {
    SwapArrowPortalParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.quadSize *= 1.5F;
        this.lifetime = (int)(Math.random() * 2.0F) + 60;
    }

    public float getQuadSize(float scaleFactor) {
        float f = 1.0F - (this.age + scaleFactor) / (this.lifetime * 1.5F);
        return this.quadSize * f;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float f = 0.94F;
            this.xd = this.xd * f;
            this.yd = this.yd * f;
            this.zd = this.zd * f;
            this.x += this.xd;
            this.y += this.yd;
            this.z += this.zd;
            this.setPos(this.x, this.y, this.z);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprites) {
            this.sprite = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SwapArrowPortalParticle swapArrowPortalParticle = new SwapArrowPortalParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            swapArrowPortalParticle.pickSprite(this.sprite);
            return swapArrowPortalParticle;
        }
    }
}
