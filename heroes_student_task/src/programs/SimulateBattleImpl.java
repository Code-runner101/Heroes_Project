package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimulateBattleImpl implements SimulateBattle {

    private PrintBattleLog printBattleLog;

    public SimulateBattleImpl() {
        this.printBattleLog = null;
    }

    public void setPrintBattleLog(PrintBattleLog printBattleLog) {
        this.printBattleLog = printBattleLog;
    }

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        List<Unit> playerUnits = new ArrayList<>(playerArmy.getUnits());
        List<Unit> computerUnits = new ArrayList<>(computerArmy.getUnits());

        while (hasAlive(playerUnits) && hasAlive(computerUnits)) {
            // Формируем очередь хода для текущего раунда
            List<Unit> allUnits = new ArrayList<>();
            for (Unit u : playerUnits) if (u.isAlive()) allUnits.add(u);
            for (Unit u : computerUnits) if (u.isAlive()) allUnits.add(u);

            // Сортировка по убыванию атаки
            allUnits.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());

            // Проходим по всем юнитам в этом раунде
            for (Unit unit : allUnits) {
                if (!unit.isAlive()) continue;

                // Атака
                Unit target = unit.getProgram().attack();
                if (target == null) continue;

                // Лог
                if (playerUnits.contains(unit)) {
                    // ход игрока
                    printBattleLog.printBattleLog(unit, target);
                } else {
                    // ход компьютера → МЕНЯЕМ МЕСТАМИ
                    printBattleLog.printBattleLog(target, unit);
                }
            }

            // Небольшая пауза между раундами для визуализации (можно убрать)
            Thread.sleep(50);
        }
    }

    private boolean hasAlive(List<Unit> units) {
        for (Unit u : units) {
            if (u.isAlive()) return true;
        }
        return false;
    }
}