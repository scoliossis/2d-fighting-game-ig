package scoliosis.GameLibs;

import scoliosis.Game;
import scoliosis.Libs.MouseLib;

import java.util.ArrayList;

public class MoveLib {


    static long sprintTicks = -1;

    static int sprintMillis = 200;

    static float directionMulti = 0;

    static long lastJump = -1;

    static boolean onGround = false;

    static long lastAirJump = -1;

    static float movementSpeed = 0.3f;

    static int clipHeight = 20;

    public static int maxDodgeTime = 300;
    public static int DodgeInvulnerabilityTime = 200;

    public static int dodgeDelay = 200;
    public static int shieldFrames = 25;


    public static ArrayList<Game.HurtBox> hurtBoxes = new ArrayList<>();

    public record Inputs(boolean upPressed, boolean upDown,
                         boolean downPressed, boolean downDown,
                         boolean leftPressed, boolean leftDown,
                         boolean rightPressed, boolean rightDown,
                         boolean jumpPressed, boolean jumpDown,
                         boolean shiftPressed, boolean shiftDown,
                         boolean leftClickPressed, boolean leftClickDown,
                         boolean rightClickPressed, boolean rightClickDown
                         ) {}


    public static void MoveLibChecks(Entity ent, Inputs inputs) {
        if (ent.lastCheckMS != 0 && ent.alive) {
            FloorPos floor = new FloorPos(0,1000);

            onGround = false;

            for (int i = 0; i < Game.hitboxes.length; i++) {
                Game.coordinates hitbox =  Game.hitboxes[i];
                if (ent.x >= hitbox.x()-(ent.width-10) && ent.x <= hitbox.x() - 5) {
                    if (hitbox.y() <= floor.y && (hitbox.y() >= ent.y || (hitbox.y() <= ent.y && hitbox.y() <= ent.y + clipHeight))) {
                        if (ent.y - clipHeight <= hitbox.y() && ent.y + 2 >= hitbox.y()) {
                            onGround = true;
                            floor.y = hitbox.y();
                        }
                    }
                }
            }

            // its possible on heavy characters to do a low jump with this and i think its cool
            if (ent.onGround && !inputs.jumpDown && !ent.landed) {
                ent.yVelo = 0;
                ent.landed = true;

                ent.xVelo *= 1.5f;
            }



            ent.hurt = System.currentTimeMillis() - ent.hitTime <= ent.hurtTime;

            ent.onGround = onGround;

            if (!ent.onGround) ent.landed = false;
            else ent.canSpecial = true;

            float fallingSpeed = 1f;


            if (!ent.hurt) {
                if (inputs.rightDown) {
                    ent.lastDirection = 1;
                    directionMulti = movementSpeed;
                }
                if (inputs.leftDown) {
                    ent.lastDirection = -1;
                    directionMulti = -movementSpeed;
                }
            }

            if (ent.canAttack) ent.renderDirection = ent.lastDirection;

            if (inputs.downPressed) ent.falling = true;

            // nesting $$$$$$
            if (ent.canAttack) {
                Entity.Attack attack;
                if (inputs.leftClickPressed || (inputs.rightClickPressed && ent.canSpecial)) {
                    if (inputs.leftClickPressed) {
                        if (ent.onGround) {
                            if (inputs.upDown) {
                                attack = Entity.up_ground;
                            } else if (inputs.downDown) {
                                attack = Entity.down_ground;
                            } else if (directionMulti != 0) {
                                attack = Entity.side_ground;
                            } else {
                                attack = Entity.up_ground;
                            }
                        } else {
                            if (inputs.upDown) {
                                attack = Entity.up_air;
                            } else if (inputs.downDown) {
                                attack = Entity.down_air;
                            } else if (directionMulti != 0) {
                                attack = Entity.side_air;
                            } else {
                                attack = Entity.up_air;
                            }
                        }
                    }
                    else {
                        if (ent.onGround) {
                            if (inputs.upDown) {
                                attack = Entity.up_ground_special;
                            } else if (inputs.downDown) {
                                attack = Entity.down_ground_special;
                            } else if (directionMulti != 0) {
                                attack = Entity.side_ground_special;
                            } else {
                                attack = Entity.up_ground_special;
                            }
                        } else {
                            if (inputs.upDown) {
                                attack = Entity.up_air_special;
                            } else if (inputs.downDown) {
                                attack = Entity.down_air_special;
                            } else if (directionMulti != 0) {
                                attack = Entity.side_air_special;
                            } else {
                                attack = Entity.up_air_special;
                            }

                            ent.canSpecial = false;
                        }
                    }

                    if (attack != null) {
                        hurtBoxes.add(new Game.HurtBox(attack.x(), attack.y(), attack.w(), attack.h(), ent.lastDirection, attack.kbX(), attack.kbY(), attack.setKB(), attack.power(), System.currentTimeMillis() + attack.startDelay(), attack.timeAroundFor(), attack.hurtStunTime(), attack.invulnerableTime(), ent));
                        ent.currentCooldown = attack.attackCooldown();
                        ent.lastAttack = attack;
                        ent.lastAttackTime = System.currentTimeMillis();

                        if (attack.multi()) {
                            ent.xVelo *= attack.velocityX();
                            ent.yVelo *= attack.velocityY();
                        }
                        else {
                            ent.xVelo = attack.velocityX() * ent.lastDirection;
                            ent.yVelo = attack.velocityY();
                        }
                    }
                }
            }

            for (Game.HurtBox hurtBox : MoveLib.hurtBoxes) {
                if (hurtBox.entity().entityID != ent.entityID && ValidHurtBox(hurtBox) && !ent.invulnerable) {
                    float hurtboxX = ((hurtBox.x()*hurtBox.direction()) + hurtBox.entity().x+((hurtBox.entity().width/2f)-(hurtBox.w()/2f)));
                    float hurtboxY = (hurtBox.y() + hurtBox.entity().y);
                    float entY = (ent.y-ent.height);

                    if ((hurtboxX >= ent.x || hurtboxX+hurtBox.w() >= ent.x) && (hurtboxX <= ent.x+ent.width || hurtboxX+hurtBox.w() <=  ent.x+ent.width)) {
                        if ((hurtboxY >= entY || hurtboxY+hurtBox.h() >= entY) && (hurtboxY <= ent.y || hurtboxX+hurtBox.h() <=  ent.y)) {
                            ent.health += hurtBox.power();

                            // math.sqrt is slow and i hate it
                            if (hurtBox.setKB()) {
                                ent.xVelo = hurtBox.kbX() * hurtBox.direction();
                                ent.yVelo = hurtBox.kbY();
                            }
                            else {
                                ent.xVelo = (float) ((hurtBox.kbX() * hurtBox.direction()) / (Math.sqrt(ent.weight))) * (ent.health / 150);
                                ent.yVelo = (float) ((hurtBox.kbY() * (entY > (hurtboxY+(hurtBox.h()/2f)) ? 1 : -1)) / (Math.sqrt(ent.weight))) * (ent.health / 150);
                            }

                            ent.hitTime = System.currentTimeMillis();
                            ent.hurtTime = hurtBox.hurtStunTime();
                            ent.invulnerableTime = hurtBox.invulnerableTime();

                            hurtBox.entity().canSpecial = true;
                            hurtBox.entity().jumpsInAir--;
                            ent.invulnerable = true;
                            ent.hurt = true;
                        }
                    }
                }
            }

            if (ent.falling) {
                fallingSpeed *= 3f;

                if (getAirTime(ent) < 100) fallingSpeed *= 4f;
            }

            if (!isSprinting() && inputs.shiftPressed && !ent.hurt && ent.canAttack) {
                if (onGround) {

                    // does sprinting
                    if (directionMulti != 0) {
                        directionMulti *= ent.weight + 1;
                        if (ent.xVelo < 0 && directionMulti > 0 || ent.xVelo > 0 && directionMulti < 0)
                            directionMulti *= 25 / ent.weight;
                    }
                    else {
                        if (System.currentTimeMillis() - ent.dodgeTime > MoveLib.maxDodgeTime+dodgeDelay) {
                            ent.dodgeTime = System.currentTimeMillis();
                            dodgeAttack(ent);
                        }
                    }
                }
                else {
                    if (System.currentTimeMillis() - ent.dodgeTime > MoveLib.maxDodgeTime+dodgeDelay && !ent.airDodged) {
                        ent.airDodged = true;
                        ent.dodgeTime = System.currentTimeMillis();


                        dodgeAttack(ent);

                        directionMulti *= ent.weight * 10;
                        if (ent.xVelo < 0 && directionMulti > 0 || ent.xVelo > 0 && directionMulti < 0)
                            directionMulti *= ent.weight * ent.weight;

                        // u can press both for new dodge!!
                        if (inputs.upDown) ent.yVelo = ent.jumpHeight;
                        if (inputs.downDown) ent.yVelo -= ent.jumpHeight;
                    }
                }

                // insane strafe bypass
                sprintTicks = System.currentTimeMillis();
            }


            if (System.currentTimeMillis() - ent.dodgeTime < DodgeInvulnerabilityTime) ent.invulnerable = true;
            // always sets u to hittable, punishing dodges
            else if (System.currentTimeMillis() - ent.hitTime < ent.invulnerableTime) ent.invulnerable = true;
            else {
                ent.invulnerable = false;
            }

            // sets x velocity
            ent.xVelo += deltaTime((directionMulti) * .005f, ent);

            if (ent.onGround) {
                ent.falling = false;
                ent.airDodged = false;
                ent.jumpsInAir = 0;
                ent.lastAirJump = 0;
            }

            if (!ent.hurt && ent.canAttack) {
                if ((ent.onGround || getAirTime(ent) < 100) && inputs.jumpPressed) {
                    lastJump = System.currentTimeMillis();
                }
                if (!ent.onGround && inputs.jumpPressed && ent.jumpsInAir < ent.airJumps && System.currentTimeMillis() - ent.lastAirJump >= 100 && getAirTime(ent) > 100) {
                    jump(ent);
                    ent.jumpsInAir++;
                    ent.lastAirJump = System.currentTimeMillis();
                }

                if ((System.currentTimeMillis() - lastJump >= ent.weight * 20L || ent.weight <= 2) && lastJump != -1) {
                    jump(ent);

                    if (System.currentTimeMillis() - ent.dodgeTime < MoveLib.DodgeInvulnerabilityTime)
                        ent.xVelo *= ent.width;
                    ent.xVelo *= 2;
                    lastJump = -1;

                }

                if (ent.xVelo > 3 || ent.xVelo < -3) {
                    if (ent.xVelo < 3) ent.xVelo = -3f;
                    else ent.xVelo = 3f;
                }
            }


            // dont clip up if hit head
            boolean hitHead = false;

            for (int i = 0; i < Game.hitboxes.length; i++) {
                Game.coordinates hitbox = Game.hitboxes[i];

                if (ent.x >= hitbox.x()-(ent.width-10) && ent.x <= hitbox.x() - 5) {
                    if (ent.y-hitbox.y() < ent.height && ent.y-hitbox.y() > clipHeight) {
                        ent.y += 1;
                        ent.yVelo = -0.1f;
                        hitHead = true;
                    }
                }

                if (ent.lastX < hitbox.x() && ent.x >= hitbox.x()) {
                    if (hitbox.y() < ent.y - 3 && hitbox.y() > ent.y + ent.height) {
                        ent.x = ent.lastX;
                    }
                }
                if (ent.lastX > hitbox.x() && ent.x <= hitbox.x()) {
                    if (hitbox.y() < ent.y - 3 && hitbox.y() > ent.y + ent.height) {
                        ent.x = ent.lastX;
                    }
                }
            }


            if (ent.y > floor.y && ent.y - clipHeight < floor.y && (ent.onGround || ent.lastOnGround) && !hitHead) {
                ent.y = floor.y;
            }

            ent.lastX = ent.x;
            ent.lastY = ent.y;
            ent.x += (ent.xVelo);
            ent.y -= deltaTime(ent.yVelo, ent) * 0.1f;



            // slowing down
            if (isSprinting()) ent.xVelo *= 0.99f;
            else ent.xVelo *= (0.99f - (0.005f * ent.weight));

            if (!onGround) {
                if (ent.yVelo < 0) ent.yVelo -= deltaTime((ent.weight * 5) * 0.00015f * fallingSpeed, ent);
                else ent.yVelo -= deltaTime((ent.weight * 5) * 0.0003f * fallingSpeed, ent);
            }

            if (ent.lastOnGround && !onGround) {
                ent.airTime = System.currentTimeMillis();
            }

            ent.canAttack = System.currentTimeMillis()-ent.dodgeTime >= maxDodgeTime;
            if (System.currentTimeMillis() - ent.lastAttackTime < ent.currentCooldown) {
                ent.canAttack = false;
            }

            if (ent.canAttack) {
                ent.lastAttack = null;
            }

            if (ent.lastAttack != null) {
                if ((ent.lastAttack.constantVeloX() || ent.lastAttack.constantVeloY()) && !ent.hurt) {
                    if (ent.lastAttack.multi()) {
                        if (ent.lastAttack.constantVeloX()) ent.xVelo *= deltaTime(ent.lastAttack.velocityX(), ent) * 0.25f;
                        if (ent.lastAttack.constantVeloY()) ent.yVelo *= deltaTime(ent.lastAttack.velocityY(), ent) * 0.25f;
                    }
                    else {
                        if (ent.lastAttack.constantVeloX()) ent.xVelo = deltaTime(ent.lastAttack.velocityX() * ent.renderDirection, ent) * 0.25f;
                        if (ent.lastAttack.constantVeloY()) ent.yVelo = deltaTime(ent.lastAttack.velocityY(), ent) * 0.25f;
                    }
                }
            }


            // cold ahh code $$$
            if (shouldDie(ent)) {
                die(ent);
            }

            ent.lastOnGround = onGround;
        }

        if (!ent.alive) {
            if (System.currentTimeMillis() - ent.diedTime > ent.deathTime) {
                respawn(ent);
            }
        }


        ent.lastCheckMS = System.currentTimeMillis();
        directionMulti = 0;
    }

    public static boolean shouldDie(Entity ent) {
        return (((ent.x < -50 || ent.x > 530 || ent.y < -100) && ent.hurt) || ent.y > 320);
    }


    public static void respawn(Entity ent) {
        ent.x = 100;
        ent.y = 30;
        ent.xVelo = 0;
        ent.yVelo = 0;
        ent.health = 0;
        ent.hitTime = System.currentTimeMillis();
        ent.invulnerableTime = 1000;
        ent.alive = true;
    }

    public static void die(Entity ent) {
        System.out.println(ent.entityID + " has died!!");
        ent.alive = false;
        ent.diedTime = System.currentTimeMillis();
    }

    public static float deltaTime(float num, Entity ent) {
        return num * (System.currentTimeMillis() - ent.lastCheckMS);
    }

    public static boolean isSprinting() {
        return sprintTicks-System.currentTimeMillis() >= -sprintMillis;
    }

    public static long getAirTime(Entity ent) {
        return System.currentTimeMillis()-ent.airTime;
    }

    public static void jump(Entity ent) {
        ent.falling = false;
        ent.onGround = false;
        ent.yVelo = ent.jumpHeight + ((ent.weight*0.1f) - 0.2f);
    }


    public static boolean ValidHurtBox(Game.HurtBox hurtBox) {
        return hurtBox.timeStart() <= System.currentTimeMillis();
    }

    public static void dodgeAttack(Entity ent) {
        hurtBoxes.add(new Game.HurtBox(Entity.dodge.x(), Entity.dodge.y(), Entity.dodge.w(), Entity.dodge.h(), ent.lastDirection, Entity.dodge.kbX(), Entity.dodge.kbY(), Entity.dodge.setKB(), Entity.dodge.power(), System.currentTimeMillis() + Entity.dodge.startDelay(), Entity.dodge.timeAroundFor(), Entity.dodge.hurtStunTime(), Entity.dodge.invulnerableTime(), ent));
        ent.currentCooldown = Entity.dodge.attackCooldown();
        ent.lastAttack = Entity.dodge;
        ent.lastAttackTime = System.currentTimeMillis();

        if (Entity.dodge.multi()) {
            ent.xVelo *= Entity.dodge.velocityX();
            ent.yVelo *= Entity.dodge.velocityY();
        }
        else {
            ent.xVelo = Entity.dodge.velocityX() * ent.lastDirection;
            ent.yVelo = Entity.dodge.velocityY();
        }
    }

}
