package uva.verspeek.hearthstone.models;

public interface Constants {

	String apiKey = "64b70b68e20490cbd3383f8d9c844a4ffd7baf9b9f6e2337006e9ac8cd825c49";
    String secretKey =  "558a6bedf1245a6b183d2977a95497ec240a1072aba900a7b5409d60fc31a932";
    
    final int CharacterSpeed = 800;	// px per second
    
    final float ratio = (128f/90f);
    final float scale = 1.3f;
    
    // Camera movement speeds
    final float maxVelocityX = 1000;
    final float maxVelocityY = 1000;
    // Camera zoom speed
    final float maxZoomFactorChange = 5;
}
