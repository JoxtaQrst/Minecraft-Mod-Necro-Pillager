package net.fabian.necropmod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

public class BossDamageSources {


    public static DamageSource getProjectileSource(ProjectileEntity projectile, Entity shooter) {
        return new ProjectileDamageSource("boss_projectile", projectile, shooter);
    }

    public static class ProjectileDamageSource extends DamageSource {
        protected ProjectileEntity projectile;
        protected Entity shooter;

        public ProjectileDamageSource(String name, ProjectileEntity projectile, Entity shooter) {
            super((RegistryEntry<DamageType>) Text.of("boss_projectile"));
            this.projectile = projectile;
            this.shooter = shooter;
        }

        public ProjectileEntity getProjectile() {
            return projectile;
        }

        public Entity getShooter() {
            return shooter;
        }
    }
}
