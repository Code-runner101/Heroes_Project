package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    // Размеры игрового поля
    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;

    // Возможные направления движения (включая диагонали)
    private static final int[][] DIRECTIONS = {
            {0, 1},   // вверх
            {1, 1},   // вверх-вправо
            {1, 0},   // вправо
            {1, -1},  // вниз-вправо
            {0, -1},  // вниз
            {-1, -1}, // вниз-влево
            {-1, 0},  // влево
            {-1, 1}   // вверх-влево
    };

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        // Создаем сет занятых клеток для быстрой проверки
        Set<String> occupiedCells = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit != attackUnit && unit != targetUnit && unit.isAlive()) {
                occupiedCells.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
            }
        }

        // Проверяем, находятся ли юниты в пределах поля
        if (!isValidPosition(attackUnit.getxCoordinate(), attackUnit.getyCoordinate()) ||
                !isValidPosition(targetUnit.getxCoordinate(), targetUnit.getyCoordinate())) {
            return new ArrayList<>();
        }

        // Если начальная и конечная точки совпадают
        if (attackUnit.getxCoordinate() == targetUnit.getxCoordinate() && attackUnit.getyCoordinate() == targetUnit.getyCoordinate()) {
            List<Edge> path = new ArrayList<>();
            path.add(new Edge(attackUnit.getxCoordinate(), attackUnit.getyCoordinate()));
            return path;
        }

        return dijkstra(attackUnit.getxCoordinate(), attackUnit.getyCoordinate(),
                targetUnit.getxCoordinate(), targetUnit.getyCoordinate(), occupiedCells);
    }

    private List<Edge> dijkstra(int startX, int startY, int targetX, int targetY,
                                Set<String> occupiedCells) {
        // Матрица расстояний
        int[][] dist = new int[WIDTH][HEIGHT];
        // Матрица для хранения предыдущих узлов
        Node[][] prev = new Node[WIDTH][HEIGHT];

        // Инициализация
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                dist[x][y] = Integer.MAX_VALUE;
                prev[x][y] = null;
            }
        }

        // Приоритетная очередь для алгоритма Дейкстры
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));

        // Начальная точка
        dist[startX][startY] = 0;
        pq.offer(new Node(startX, startY, 0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int x = current.x;
            int y = current.y;

            // Если достигли цели, восстанавливаем путь
            if (x == targetX && y == targetY) {
                return reconstructPath(prev, targetX, targetY);
            }

            // Пропускаем если нашли более короткий путь
            if (current.distance > dist[x][y]) {
                continue;
            }

            // Проверяем всех соседей
            for (int[] dir : DIRECTIONS) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                // Проверяем валидность позиции и доступность
                if (isValidPosition(newX, newY) && !occupiedCells.contains(newX + "," + newY)) {
                    // Стоимость движения: 10 для ортогонального, 14 для диагонального (√2 ≈ 1.414)
                    int cost = (Math.abs(dir[0]) + Math.abs(dir[1]) == 2) ? 14 : 10;
                    int newDist = dist[x][y] + cost;

                    if (newDist < dist[newX][newY]) {
                        dist[newX][newY] = newDist;
                        prev[newX][newY] = new Node(x, y, newDist);
                        pq.offer(new Node(newX, newY, newDist));
                    }
                }
            }
        }

        // Путь не найден
        return new ArrayList<>();
    }

    private List<Edge> reconstructPath(Node[][] prev, int targetX, int targetY) {
        List<Edge> path = new ArrayList<>();
        int x = targetX;
        int y = targetY;

        // Восстанавливаем путь от цели к началу
        Stack<Edge> reversePath = new Stack<>();
        while (prev[x][y] != null) {
            reversePath.push(new Edge(x, y));
            Node prevNode = prev[x][y];
            x = prevNode.x;
            y = prevNode.y;
        }

        // Добавляем начальную точку
        reversePath.push(new Edge(x, y));

        // Разворачиваем путь
        while (!reversePath.isEmpty()) {
            path.add(reversePath.pop());
        }

        return path;
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    // Вспомогательный класс для узлов
    private static class Node {
        int x;
        int y;
        int distance;

        Node(int x, int y, int distance) {
            this.x = x;
            this.y = y;
            this.distance = distance;
        }
    }
}