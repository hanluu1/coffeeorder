import java.util.ArrayList;
import java.util.List;

public class Espresso implements Coffee {
        @Override
        public double getCost() {
                return 1.75;
        }

        @Override
        public List<String> getIngredients() {
            List<String> ingredients = new ArrayList<>();
            return ingredients;
        }

        @Override
        public String printCoffee() {
                return "An espresso";
        }
}

