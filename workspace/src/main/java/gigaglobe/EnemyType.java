package gigaglobe;

public enum EnemyType {
    PREY,
    PREDATOR,
    NEUTRAL
}
/*
 Pray:  The enemy is bad with its width so it might shrink soon and it is in danger of being eaten by a predator,
        it will give priority to the distance only
 Predator:  The enemy is good with its width so it won't shrink too soon.
            path is decided by the pray's width, the distance comes later
 Neutral: The enemy is too small and it cannot be a Predator anymore
          so it will become an obsolete pray (which cannot move - Will rest in a static position and it won't shrink)
          must be <25
*/