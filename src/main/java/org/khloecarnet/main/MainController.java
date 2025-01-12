package org.khloecarnet.main;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML
    private GridPane gridPane;
    @FXML
    private Button btnCompute;
    @FXML
    private Label prob0;
    @FXML
    private Label prob1;
    @FXML
    private Label prob2;
    @FXML
    private Label prob3;
    @FXML
    private Label warning;
    @FXML
    private Button btnReset;

    private final int rows = 4;
    private final int cols = 4;
    private final boolean[][] gridState = new boolean[rows][cols];

    @FXML
    private void initialize(){
        // 动态创建 4x4 的 Label 网格
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // 创建 Label
                Label label = new Label("O");
                label.setAlignment(Pos.CENTER);
                label.setPrefWidth(100);
                label.setPrefHeight(40);
                label.setFont(Font.font("Comic Sans MS", 25));
                label.setTextFill(Color.RED); // 初始颜色为红色

                // 设置点击事件
                int finalRow = row; // 必须使用 final 或 effectively final 变量
                int finalCol = col;
                label.setOnMouseClicked(event -> {
                    // 切换状态
                    gridState[finalRow][finalCol] = !gridState[finalRow][finalCol];

                    // 根据状态更新 Label
                    if (gridState[finalRow][finalCol]) {
                        label.setText("X");
                        label.setTextFill(Color.GREEN); // 点亮状态颜色
                    } else {
                        label.setText("O");
                        label.setTextFill(Color.RED); // 未点亮状态颜色
                    }
                });

                // 将 Label 添加到 GridPane 的指定位置
                gridPane.add(label, col, row);
            }
        }

        btnCompute.setOnAction(event -> {
            computeProbability();
        });

        btnReset.setOnAction(event -> {
            resetGrid();
        });

    }

    private void computeProbability(){

        List<int[]> litCells = new ArrayList<>();
        List<int[]> unlitCells = new ArrayList<>();
        for (int row = 0; row < rows; row++){
            for (int col = 0; col < cols; col++){
                if (gridState[row][col]) {
                    litCells.add(new int[]{row, col});
                } else {
                    unlitCells.add(new int[]{row, col});
                }
            }
        }
        int litCount = litCells.size();
        int remaining = 9 - litCount;
        if (remaining < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("贴纸数量超出限制！");

            alert.showAndWait();
            return;
        }

        int totalCombinations = combination(unlitCells.size(), remaining);
        int zeroLines = 0, oneLine = 0, twoLines = 0, threeLines = 0;

        // 模拟剩余格子的所有组合
        List<List<int[]>> combinations = generateCombinations(unlitCells, remaining);
        for (List<int[]> combination : combinations) {
            // 模拟点亮剩余格子
            boolean[][] simulatedGrid = new boolean[rows][cols];
            for (int[] cell : litCells) {
                simulatedGrid[cell[0]][cell[1]] = true;
            }
            for (int[] cell : combination) {
                simulatedGrid[cell[0]][cell[1]] = true;
            }

            // 计算直线数
            int lineCount = countLines(simulatedGrid);
            switch (lineCount) {
                case 0 -> zeroLines++;
                case 1 -> oneLine++;
                case 2 -> twoLines++;
                case 3 -> threeLines++;
            }
        }

        // 计算概率
        double zeroProb = (double) zeroLines / totalCombinations * 100;
        double oneProb = (double) oneLine / totalCombinations * 100;
        double twoProb = (double) twoLines / totalCombinations * 100;
        double threeProb = (double) threeLines / totalCombinations * 100;

        prob0.setText(String.format("%.2f",zeroProb) + "%");
        prob1.setText(String.format("%.2f",oneProb) + "%");
        prob2.setText(String.format("%.2f",twoProb) + "%");
        prob3.setText(String.format("%.2f",threeProb) + "%");

        if (threeProb == 0) {
            warning.setVisible(true);
        } else {
            warning.setVisible(false);
        }

    }

    // 计算组合
    private int combination(int n, int k) {
        if (k > n) return 0;
        if (k == 0 || k == n) return 1;
        return combination(n - 1, k - 1) + combination(n - 1, k);
    }

    // 生成组合
    private List<List<int[]>> generateCombinations(List<int[]> elements, int k) {
        List<List<int[]>> result = new ArrayList<>();
        generateCombinationsHelper(elements, k, 0, new ArrayList<>(), result);
        return result;
    }

    private void generateCombinationsHelper(List<int[]> elements, int k, int start, List<int[]> current, List<List<int[]>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < elements.size(); i++) {
            current.add(elements.get(i));
            generateCombinationsHelper(elements, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    // 统计直线数量
    private int countLines(boolean[][] grid) {
        int lines = 0;

        // 检查行
        for (int row = 0; row < rows; row++) {
            if (grid[row][0] && grid[row][1] && grid[row][2] && grid[row][3]) {
                lines++;
            }
        }

        // 检查列
        for (int col = 0; col < cols; col++) {
            if (grid[0][col] && grid[1][col] && grid[2][col] && grid[3][col]) {
                lines++;
            }
        }

        // 检查对角线
        if (grid[0][0] && grid[1][1] && grid[2][2] && grid[3][3]) {
            lines++;
        }
        if (grid[0][3] && grid[1][2] && grid[2][1] && grid[3][0]) {
            lines++;
        }

        return lines;
    }

    private void resetGrid(){
        for (int row = 0; row < rows; row++){
            for (int col = 0; col < cols; col++) {
                // 重置数组
                gridState[row][col] = false;

                // 重置表格标签
                Label label = (Label) getNodeFromGridPane(gridPane, col, row);
                if (label != null){
                    label.setText("O");
                    label.setTextFill(Color.RED);
                }
            }
        }

        prob0.setText("41.54%");
        prob1.setText("47.90%");
        prob2.setText("10.35%");
        prob3.setText("0.21%");

        warning.setVisible(false);
    }

    private javafx.scene.Node getNodeFromGridPane(GridPane gridPane, int row, int col){
        for (javafx.scene.Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null
                    && GridPane.getColumnIndex(node) == col
                    && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;

    }



}
