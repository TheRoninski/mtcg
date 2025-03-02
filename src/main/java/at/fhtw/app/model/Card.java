package at.fhtw.app.model;

public record Card(String id, String name, float damage, CardType type, Element element) {
    public Card {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be less than 0.");
        }
    }

    @Override
    public String toString() {
        return "Card(Name: " + name + ", Damage: " + damage + ")";
    }
}
