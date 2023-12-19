import java.util.ArrayList;
import java.util.List;

public class BlackCoffee implements Coffee{
    public double getCost(){
        return 1.00;
    }
    @Override
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>();
        return ingredients;
    }

    @Override
    public String printCoffee() {
        return "A black coffee";
    }
}
