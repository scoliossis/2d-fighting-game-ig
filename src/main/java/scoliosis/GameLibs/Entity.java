package scoliosis.GameLibs;

public class Entity {
    public float x;
    public float y;
    public float lastX;
    public float lastY;

    public boolean landed;
    public boolean onGround;
    public boolean lastOnGround;
    public long airTime;

    public float xVelo;
    public float yVelo;
    public int width;
    public int height;
    public float weight;

    public long dodgeTime;
    public boolean invulnerable;

    public boolean canAttack;

    public Attack lastAttack;
    public long lastAttackTime;

    public long currentCooldown;
    public int lastDirection = -1;
    public int renderDirection = -1;
    public int entityID;

    public int airJumps;

    public float jumpHeight;

    public long lastCheckMS = 0;

    public float health = 0;

    public long hitTime;
    public long hurtTime;

    public boolean hurt;
    public long invulnerableTime;

    public boolean falling;
    public boolean airDodged;
    public int jumpsInAir = -1;
    public long lastAirJump;
    public boolean alive = true;
    public long diedTime;
    public int deathTime = 1000;
    public boolean canSpecial;

    public MoveLib.Inputs inputs = new MoveLib.Inputs(false,false,false,false,false,false,false,false,false,false,false,false, false, false, false, false);

    public Entity(float x, float y, float xVelo, float yVelo, int width, int height, float weight, int airJumps, float jumpHeight, int entityID) {
        this.x = x;
        this.y = y;

        this.lastX = x;
        this.lastY = y;

        this.xVelo = xVelo;
        this.yVelo = yVelo;

        this.width = width;
        this.height = height;
        this.weight = weight;

        this.jumpHeight = jumpHeight;
        this.airJumps = airJumps;

        this.entityID = entityID;
    }


    public static String getAnimationStep(Entity ent) {
        String filePath = "characters\\mushroom\\";

        if (ent.lastAttack != null) {
            filePath += "attacks\\";

            if (ent.lastAttack == dodge) {
                filePath += "dodge\\";

                int frames = 18;



                return filePath + "dodge-"+(int) (((float) (System.currentTimeMillis() - ent.lastAttackTime) / ent.currentCooldown * frames)+1);
            }



            // air
            if (ent.lastAttack == up_air) {
                filePath += "up_air\\";
                int frames = 14;

                return filePath + "up_air-"+(int) (((float) (System.currentTimeMillis() - ent.lastAttackTime) / ent.currentCooldown * frames)+1);
            }

            if (ent.lastAttack == down_air) {
                filePath += "down_air\\";
                int frames = 17;

                return filePath + "down_air-"+(int) (((float) (System.currentTimeMillis() - ent.lastAttackTime) / ent.currentCooldown * frames)+1);
            }
            if (ent.lastAttack == side_air) {
                filePath += "side_air\\";
                int frames = 8;

                return filePath + "side_air-"+(int) (((float) (System.currentTimeMillis() - ent.lastAttackTime) / ent.currentCooldown * frames)+1);
            }

            // air specials
            if (ent.lastAttack == up_air_special) {
                filePath += "up_air_special\\";
                int frames = 16;

                return filePath + "up_air_special-"+(int) (((float) (System.currentTimeMillis() - ent.lastAttackTime) / ent.currentCooldown * frames)+1);
            }

            if (ent.lastAttack == down_air_special) {
                filePath += "down_air_special\\";
                int frames = 18;

                return filePath + "down_air_special-"+(int) (((float) (System.currentTimeMillis() - ent.lastAttackTime) / ent.currentCooldown * frames)+1);
            }
            if (ent.lastAttack == side_air_special) {
                filePath += "side_air_special\\";
                int frames = 5;

                return filePath + "side_air_special-"+(int) (((float) (System.currentTimeMillis() - ent.lastAttackTime) / ent.currentCooldown * frames)+1);
            }



            // ground attackie
            if (ent.lastAttack == up_ground) {
                filePath += "up_ground\\";
                int frames = 14;

                return filePath + "up_ground-"+(int) (((float) (System.currentTimeMillis() - ent.lastAttackTime) / ent.currentCooldown * frames)+1);
            }

            if (ent.lastAttack == down_ground) {
                filePath += "down_ground\\";
                int frames = 11;

                return filePath + "down_ground-"+(int) (((float) (System.currentTimeMillis() - ent.lastAttackTime) / ent.currentCooldown * frames)+1);
            }
            if (ent.lastAttack == side_ground) {
                filePath += "side_ground\\";
                int frames = 8;

                return filePath + "side_ground-"+(int) (((float) (System.currentTimeMillis() - ent.lastAttackTime) / ent.currentCooldown * frames)+1);
            }


            // ground attack special
            if (ent.lastAttack == up_ground_special) {
                filePath += "up_ground_special\\";
                int frames = 14;

                return filePath + "up_ground_special-"+(int) (((float) (System.currentTimeMillis() - ent.lastAttackTime) / ent.currentCooldown * frames)+1);
            }

            if (ent.lastAttack == down_ground_special) {
                filePath += "down_ground_special\\";
                int frames = 16;

                return filePath + "down_ground_special-"+(int) (((float) (System.currentTimeMillis() - ent.lastAttackTime) / ent.currentCooldown * frames)+1);
            }
            if (ent.lastAttack == side_ground_special) {
                filePath += "side_ground_special\\";
                int frames = 14;

                return filePath + "side_ground_special-"+(int) (((float) (System.currentTimeMillis() - ent.lastAttackTime) / ent.currentCooldown * frames)+1);
            }
        }

        return filePath + "idol\\idol-1";
        //return filePath + "idol\\idol-"+(int) (((System.currentTimeMillis() * 0.01) % 4)+1);
    }

    public record Attack(float x, float y, int w, int h, float kbX, float kbY, boolean setKB, float power, int startDelay, long timeAroundFor, long hurtStunTime, long invulnerableTime, int attackCooldown, boolean multi, float velocityX, float velocityY, boolean constantVeloX, boolean constantVeloY) {

    }

    // NORMALS
    //public static Attack up_ground = new Attack(5, -40, 28,50,2f, 0.7f, false,15, 550, 100, 700, 100, 600, false, 0, 1f, true, false);
    public static Attack up_ground = new Attack(10, -8, 10,8,2f, 0.7f, false,13, 250, 100, 700, 300, 400, false, 0, 0.4f, true, false);
    public static Attack down_ground = new Attack(10, -5, 20,5,0, 0.5f, true,3, 0, 200, 500, 50, 300, false, 0.35f, 0, true, false);
    public static Attack side_ground = new Attack(15, -15, 5,5,1f, 0f, false,11, 250, 50, 200, 50, 200, false, -0.15f, 0.5f, true, false);

    public static Attack up_air = new Attack(0, -40, 40,20,0, -2f, false,3, 0, 100, 100, 100, 150, false, 0, -0.6f, true, true);
    public static Attack down_air = new Attack(10, -5, 15,20,0.6f, -0.75f, true,0.3f, 0, 300, 500, 300, 700, false, 0.3f, -0.3f, true, true);
    public static Attack side_air = new Attack(15, -15, 5,5,1f, -0.3f, false,11, 50, 50, 200, 100, 400, false, 0.3f, 0, false, false);






    // SPECIALS
    public static Attack up_air_special = new Attack(0, -40, 40,30,0, 2f, true,10, 100, 300, 300, 300, 1000, false, 0, 1.5f, false, false);
    public static Attack down_air_special = new Attack(0, -20, 40,30,0.1f, -1f, false,15f, 200, 600, 600, 700, 1000, false, 0, -0.3f, true, false);
    public static Attack side_air_special = new Attack(0, -10, 20,10,0.6f, -0.7f, true,3, 200, 600, 200, 50, 700, false, 0.5f, 0.1f, true, false);

    public static Attack up_ground_special = new Attack(15, -20, 5,10,-0.1f, 0.1f, true,0.1f, 100, 300, 50, 0, 400, false, 0, 0, true, true);
    public static Attack down_ground_special = new Attack(0, -10, 40,10,2f, -2f, false,5f, 200, 700, 200, 100, 700, false, 0, 0, true, true);
    public static Attack side_ground_special = new Attack(15, -15, 5,5,1.5f, 0.3f, false,10, 300, 300, 300, 0, 600, false, 0, 0, true, true);


    // EXTRA
    public static Attack dodge = new Attack(0, -40, 40,40,1f, 1f, false,1, 0, MoveLib.DodgeInvulnerabilityTime, MoveLib.DodgeInvulnerabilityTime, MoveLib.maxDodgeTime, MoveLib.maxDodgeTime, true, 1, 1, false, false);
    //public static Attack tap = new Attack(0, -40, 40,10,0, 4f, false,3, 300, 100, 5000, 100, 3000, false, 0, 1f, true, false);
    //public static Attack inf_combo = new Attack(0, -40, 40,20,0, -2f, false,3, 0, 100, 300, 100, 150, false, 0, -0.6f, true, true);

    //public static Attack up_air_special = new Attack(10, -40, 10,40,0.6f, 0.2f, true,1, 0, 500, 50, 0, 1000, false, 0.6f, 1f, false, false);
    //public static Attack down_air_special = new Attack(10, -5, 15,20,0.6f, -0.75f, true,0.3f, 0, 700, 200, 0, 1000, false, 0.6f, -1f, true, true);
    //public static Attack side_air_special = new Attack(0, -40, 40,40,0.5f, -0.7f, true,1, 300, 600, 200, 0, 2000, false, 0.5f, -0.7f, true, false);

}
