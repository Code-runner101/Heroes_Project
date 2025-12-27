package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {


    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();
        int rows = unitsByRow.size();

        if (isLeftArmyTarget) {
            // Атакуем левую армию, проверяем "не закрыт справа"
            for (int r = 0; r < rows; r++) {
                List<Unit> row = unitsByRow.get(r);
                for (int c = 0; c < row.size(); c++) {
                    Unit u = row.get(c);
                    if (u == null || !u.isAlive()) continue;

                    // Проверяем, есть ли справа юнит
                    boolean blocked = false;
                    if (c + 1 < row.size()) {
                        Unit right = row.get(c + 1);
                        blocked = right != null && right.isAlive();
                    }

                    if (!blocked) {
                        suitableUnits.add(u);
                    }
                }
            }
        } else {
            // Атакуем правую армию, проверяем "не закрыт слева"
            for (int r = 0; r < rows; r++) {
                List<Unit> row = unitsByRow.get(r);
                for (int c = 0; c < row.size(); c++) {
                    Unit u = row.get(c);
                    if (u == null || !u.isAlive()) continue;

                    // Проверяем, есть ли слева юнит
                    boolean blocked = false;
                    if (c - 1 >= 0) {
                        Unit left = row.get(c - 1);
                        blocked = left != null && left.isAlive();
                    }

                    if (!blocked) {
                        suitableUnits.add(u);
                    }
                }
            }
        }

        return suitableUnits;
    }
}