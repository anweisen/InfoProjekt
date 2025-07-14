package game.tower;

import game.enemy.Enemy;
import game.state.GameState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public enum TowerTargetSelector {

    NEAREST("Nächster") {
        @Override
        public double calculateScore(AbstractTower tower, Enemy enemy) {
            return tower.distanceTo(enemy);
        }

        @Override
        public boolean isScoreBetter(double originalScore, double newScore) {
            return newScore < originalScore;
        }

        @Override
        public double getInitialScore() {
            return Double.MAX_VALUE;
        }
    },
    FIRST("Erster") {
        @Override
        public double calculateScore(AbstractTower tower, Enemy enemy) {
            return enemy.getWaypointCounter();
        }

        @Override
        public boolean isScoreBetter(double originalScore, double newScore) {
            return newScore > originalScore;
        }

        @Override
        public double getInitialScore() {
            return Double.MIN_VALUE;
        }
    },
    LAST("Letzter") {
        @Override
        public double calculateScore(AbstractTower tower, Enemy enemy) {
            return enemy.getWaypointCounter();
        }

        @Override
        public boolean isScoreBetter(double originalScore, double newScore) {
            return newScore < originalScore;
        }

        @Override
        public double getInitialScore() {
            return Double.MAX_VALUE;
        }
    },
    STRONGEST("Stärkster") {
        @Override
        public double calculateScore(AbstractTower tower, Enemy enemy) {
            return enemy.getHealth();
        }

        @Override
        public boolean isScoreBetter(double originalScore, double newScore) {
            return newScore > originalScore;
        }

        @Override
        public double getInitialScore() {
            return Double.MIN_VALUE;
        }
    },
    WEAKEST("Schwächster") {
        @Override
        public double calculateScore(AbstractTower tower, Enemy enemy) {
            return enemy.getHealth();
        }

        @Override
        public boolean isScoreBetter(double originalScore, double newScore) {
            return newScore < originalScore;
        }

        @Override
        public double getInitialScore() {
            return Double.MAX_VALUE;
        }
    },
    UNSUPPORTED("") {
        @Override
        public double calculateScore(AbstractTower tower, Enemy enemy) {
            return 0;
        }

        @Override
        public boolean isScoreBetter(double originalScore, double newScore) {
            return false;
        }

        @Override
        public double getInitialScore() {
            return 0;
        }

        @Override
        public List<Enemy> findTargets(GameState state, AbstractTower tower, int maxTargets) {
            List<Enemy> targets = new ArrayList<>(state.getEnemies());
            targets.removeIf(tower::isInvalidTarget);
            return targets;
        }
    };

    public static final TowerTargetSelector[] DEFAULT_OPTIONS = {NEAREST, FIRST, LAST, STRONGEST, WEAKEST};

    private final String displayName;

    TowerTargetSelector(String displayName) {
        this.displayName = displayName;
    }

    public abstract double calculateScore(AbstractTower tower, Enemy enemy);

    public abstract boolean isScoreBetter(double originalScore, double newScore);

    public abstract double getInitialScore();

    public String getDisplayName() {
        return displayName;
    }

    public Enemy findTarget(GameState state, AbstractTower tower) {
        Enemy bestEnemy = null;
        double bestScore = getInitialScore();

        for (Enemy enemy : state.getEnemies()) {
            if (tower.isInvalidTarget(enemy)) continue;

            double score = calculateScore(tower, enemy);
            if (bestEnemy == null || isScoreBetter(bestScore, score)) {
                bestEnemy = enemy;
                bestScore = score;
            }
        }

        return bestEnemy;
    }

    public List<Enemy> findTargets(GameState state, AbstractTower tower, int maxTargets) {
        List<Enemy> targets = new ArrayList<>(state.getEnemies());
        targets.removeIf(tower::isInvalidTarget);
        if (maxTargets < 0) return targets;
        targets.sort(new TargetScoreComparator(tower));
        return targets.subList(0, Math.min(maxTargets, targets.size()));
    }

    public static void cycleTargetSelector(AbstractTower tower) {
        TowerTargetSelector[] options = tower.getPossibleTargetSelectors();
        if (options == null || options.length < 2) return;
        int currentIndex = -1;
        for (int i = 0; i < options.length; i++) {
            if (options[i] == tower.getTargetSelector()) {
                currentIndex = i;
                break;
            }
        }
        if (currentIndex == -1) return;
        int nextIndex = (currentIndex + 1) % options.length;
        tower.setTargetSelector(options[nextIndex]);
    }

    public class TargetScoreComparator implements Comparator<Enemy> {
        private final AbstractTower tower;

        public TargetScoreComparator(AbstractTower tower) {
            this.tower = tower;
        }

        @Override
        public int compare(Enemy e1, Enemy e2) {
            double score1 = calculateScore(tower, e1);
            double score2 = calculateScore(tower, e2);
            if (score1 == score2) return 0;
            return isScoreBetter(score1, score2) ? -1 : 1;
        }
    }

}
