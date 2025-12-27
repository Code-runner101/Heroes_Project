package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    private static final int WIDTH = 3;
    private static final int HEIGHT = 21;
    private static final int MAX_PER_TYPE = 11;

    private final Random random = new Random();

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        Army army = new Army();
        boolean[][] occupied = new boolean[WIDTH][HEIGHT];
        Map<String, Integer> typeCount = new HashMap<>();
        int remainingPoints = maxPoints;

        // Находим минимальную стоимость юнита
        int cheapestCost = Integer.MAX_VALUE;
        for (Unit u : unitList) {
            if (u.getCost() < cheapestCost) {
                cheapestCost = u.getCost();
            }
        }

        while (remainingPoints >= cheapestCost) {
            // Отбираем кандидатов, которые умещаются в бюджет и не превышают MAX_PER_TYPE
            List<Unit> candidates = new ArrayList<>();
            for (Unit u : unitList) {
                int count = typeCount.getOrDefault(u.getUnitType(), 0);
                if (u.getCost() <= remainingPoints && count < MAX_PER_TYPE) {
                    candidates.add(u);
                }
            }

            if (candidates.isEmpty()) break;

            // Находим максимальную эффективность
            double maxEfficiency = candidates.stream()
                    .mapToDouble(u -> (double) (u.getBaseAttack() + u.getHealth()) / u.getCost())
                    .max().orElse(0);

            // Отбираем лучших кандидатов
            List<Unit> bestUnits = new ArrayList<>();
            for (Unit u : candidates) {
                double efficiency = (double) (u.getBaseAttack() + u.getHealth()) / u.getCost();
                if (Double.compare(efficiency, maxEfficiency) == 0) {
                    bestUnits.add(u);
                }
            }

            // Выбираем случайного юнита среди лучших
            Unit selected = bestUnits.get(random.nextInt(bestUnits.size()));

            // Находим свободную клетку
            int[] cell = selectCell(occupied);
            if (cell == null) break; // нет свободного места

            // Создаем копию юнита с уникальным именем и координатами
            int idx = typeCount.getOrDefault(selected.getUnitType(), 0) + 1;
            Unit newUnit = new Unit(
                    selected.getUnitType() + " " + idx,
                    selected.getUnitType(),
                    selected.getHealth(),
                    selected.getBaseAttack(),
                    selected.getCost(),
                    selected.getAttackType(),
                    selected.getAttackBonuses(),
                    selected.getDefenceBonuses(),
                    cell[0],
                    cell[1]
            );

            army.getUnits().add(newUnit);
            occupied[cell[0]][cell[1]] = true;
            typeCount.put(selected.getUnitType(), idx);
            remainingPoints -= selected.getCost();
        }

        army.setPoints(maxPoints - remainingPoints);
        return army;
    }

    /**
     * Выбор свободной клетки на игровом поле.
     * @param arr двумерный массив, где true = занято, false = свободно
     * @return координаты [x, y] или null если свободных клеток нет
     */
    private int[] selectCell(boolean[][] arr) {
        List<int[]> freeCells = new ArrayList<>();
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (!arr[x][y]) freeCells.add(new int[]{x, y});
            }
        }
        if (freeCells.isEmpty()) return null;
        return freeCells.get(random.nextInt(freeCells.size()));
    }
}