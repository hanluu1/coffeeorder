import java.util.List;

public class WithWhippedCream extends CoffeeDecorator {
    public WithWhippedCream(Coffee c) {
        super(c);
    }

    @Override
    public double getCost() {
        return super.getCost() + 0.25;
    }

    @Override
    public List<String> getIngredients() {
        List<String> ingredients = super.getIngredients();
        return ingredients;
    }

    @Override
    public String printCoffee() {
        return super.printCoffee() + " with whipped cream";
    }
}
