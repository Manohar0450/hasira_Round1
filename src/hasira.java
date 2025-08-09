import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;

public class FindCoefficientC {

    public static void main(String[] args) {
        try {
            // 1. Read the entire JSON file into a string
// Make sure the filename inside the quotes matches your file exactly
String content = new String(Files.readAllBytes(Paths.get("hasira.json")));
            // 2. Parse the string into a JSON object
            JSONObject json = new JSONObject(content);

            // 3. Extract k (the number of points to use)
            int k = json.getJSONObject("keys").getInt("k");
            System.out.println("Using k = " + k + " points for interpolation.");

            // 4. Extract the points (x, y)
            // x will be 1, 2, 3, ... k
            // y will be the 'value' converted from its 'base'
            BigInteger[] y_coords = new BigInteger[k];

            for (int i = 0; i < k; i++) {
                String pointKey = String.valueOf(i + 1); // JSON keys are "1", "2", ...
                JSONObject pointData = json.getJSONObject(pointKey);
                
                String valueStr = pointData.getString("value");
                int base = Integer.parseInt(pointData.getString("base"));
                
                // Convert the value string from its base to a BigInteger (base-10)
                y_coords[i] = new BigInteger(valueStr, base);
            }

            // 5. Calculate 'c' using the simplified Lagrange formula
            // c = Sum [ y_j * (-1)^(j-1) * C(k, j) ] for j = 1 to k
            BigInteger c = BigInteger.ZERO;

            for (int j = 1; j <= k; j++) {
                // Get the y-coordinate for the current point j.
                // Our array is 0-indexed, so we use y_coords[j-1].
                BigInteger y_j = y_coords[j-1];

                // Calculate the binomial coefficient C(k, j)
                BigInteger combinations = combinations(k, j);

                // Calculate the term: y_j * C(k, j)
                BigInteger term = y_j.multiply(combinations);
                
                // Apply the sign: (-1)^(j-1)
                // If (j-1) is odd (meaning j is even), negate the term.
                if (j % 2 == 0) {
                    term = term.negate();
                }

                // Add the term to the total sum for 'c'
                c = c.add(term);
            }

            // 6. Print the final result
            System.out.println("\nCalculation complete.");
            System.out.println("The value of the constant term 'c' is:");
            System.out.println(c);

        } catch (IOException e) {
            System.err.println("Error reading the input.json file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An error occurred during processing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Calculates the binomial coefficient "n choose k" (C(n, k)) using BigInteger.
     * This is an efficient way to compute it to avoid large intermediate factorial values.
     * C(n, k) = n * (n-1) * ... * (n-k+1) / k!
     * @param n The total number of items.
     * @param k The number of items to choose.
     * @return The result of C(n, k) as a BigInteger.
     */
    public static BigInteger combinations(int n, int k) {
        if (k < 0 || k > n) {
            return BigInteger.ZERO;
        }
        if (k == 0 || k == n) {
            return BigInteger.ONE;
        }
        // Use the symmetry C(n, k) == C(n, n-k) to speed up calculation
        if (k > n / 2) {
            k = n - k;
        }

        BigInteger result = BigInteger.ONE;
        for (int i = 1; i <= k; i++) {
            result = result.multiply(BigInteger.valueOf(n - i + 1)).divide(BigInteger.valueOf(i));
        }
        return result;
    }
}