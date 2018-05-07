package part3.voyager.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import org.jdom.JDOMException;

/**
 * Главный фрейм
 *
 * @author Михаил Глухих
 */
public class VoyagerFrame extends JFrame {
    /**
     * Текущий файл
     */
    private File currentFile;
    /**
     * Компоненты-дети
     */
    private VoyagerPanel voyagerPanel;
    private InfoPanel infoPanel;
    private JLabel statusLabel;
    /**
     * Слушатели
     */
    private ActionListener addCityListener, addWayListener, selectListener;
    private ActionListener openListener, saveListener, quitListener;
    private ActionListener undoListener, redoListener;
    private JMenuItem undoMenu, redoMenu;

    private UndoManager undoManager = new UndoManager() {
        @Override
        public synchronized boolean addEdit(UndoableEdit anEdit) {
            boolean result = super.addEdit(anEdit);
            updateUndoRedoStatus();
            return result;
        }
    };

    private void updateUndoRedoStatus() {
        if (undoManager.canUndo()) {
            undoMenu.setEnabled(true);
            undoMenu.setText(undoManager.getUndoPresentationName());
        }
        else {
            undoMenu.setEnabled(false);
            undoMenu.setText("Отменить действие");
        }
        if (undoManager.canRedo()) {
            redoMenu.setEnabled(true);
            redoMenu.setText(undoManager.getRedoPresentationName());
        }
        else {
            redoMenu.setEnabled(false);
            redoMenu.setText("Повторить действие");
        }
    }

    /**
     * Инициализация информационной панели
     */
    private void initInfoPanel() {
        infoPanel = new InfoPanel(null);
        infoPanel.setPreferredSize(new Dimension(200, 500));
        infoPanel.setMinimumSize(new Dimension(150, 500));
        infoPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
    }

    /**
     * Инициализация панели статуса
     */
    private void initStatusBar() {
        JPanel statusBar = new JPanel();
        statusBar.setPreferredSize(new Dimension(500, 25));
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Режим выбора");
        statusBar.add(statusLabel);
        this.add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * Инициализация главной панели
     */
    private void initMainPanel() {
        voyagerPanel = new VoyagerPanel(infoPanel, undoManager);
        voyagerPanel.setBackground(new Color(0, 0, 64));
        voyagerPanel.setPreferredSize(new Dimension(1000, 1000));
        voyagerPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
    }

    /**
     * Инициализация меню
     */
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        JMenuItem openMenu = new JMenuItem("Открыть");
        openMenu.addActionListener(openListener);
        fileMenu.add(openMenu);
        JMenuItem saveMenu = new JMenuItem("Сохранить");
        saveMenu.addActionListener(saveListener);
        fileMenu.add(saveMenu);
        fileMenu.addSeparator();
        JMenuItem quitMenu = new JMenuItem("Выйти");
        quitMenu.addActionListener(quitListener);
        fileMenu.add(quitMenu);
        JMenu editMenu = new JMenu("Редактирование");
        menuBar.add(editMenu);
        undoMenu = new JMenuItem("");
        undoMenu.addActionListener(undoListener);
        editMenu.add(undoMenu);
        redoMenu = new JMenuItem("");
        redoMenu.addActionListener(redoListener);
        editMenu.add(redoMenu);
        updateUndoRedoStatus();
        JMenu modeMenu = new JMenu("Режим");
        ButtonGroup modeGroup = new ButtonGroup();
        JRadioButtonMenuItem selectMenu = new JRadioButtonMenuItem("Выбор");
        selectMenu.setSelected(true);
        selectMenu.addActionListener(selectListener);
        modeMenu.add(selectMenu);
        modeGroup.add(selectMenu);
        JRadioButtonMenuItem addCityMenu = new JRadioButtonMenuItem("Добавить город");
        addCityMenu.addActionListener(addCityListener);
        modeMenu.add(addCityMenu);
        modeGroup.add(addCityMenu);
        JRadioButtonMenuItem addWayMenu = new JRadioButtonMenuItem("Добавить путь");
        addWayMenu.addActionListener(addWayListener);
        modeMenu.add(addWayMenu);
        modeGroup.add(addWayMenu);
        menuBar.add(modeMenu);
    }

    /**
     * Инициализация панели инструментов
     */
    private void initToolBar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setBorder(new BevelBorder(BevelBorder.RAISED));
        toolbar.addSeparator();
        JButton openButton = new JButton(new ImageIcon("files/open.png"));
        openButton.addActionListener(openListener);
        toolbar.add(openButton);
        JButton saveButton = new JButton(new ImageIcon("files/save.png"));
        saveButton.addActionListener(saveListener);
        toolbar.add(saveButton);
        toolbar.addSeparator();
        JButton quitButton = new JButton(new ImageIcon("files/quit.png"));
        quitButton.addActionListener(quitListener);
        toolbar.add(quitButton);
        this.add(toolbar, BorderLayout.NORTH);
    }

    /**
     * Инициализация всех слушателей
     */
    private void initListeners() {
        // Режим добавления города
        addCityListener = e -> onAddCity();
        // Режим добавления пути
        addWayListener = e -> onAddWay();
        // Режим выбора
        selectListener = e -> onSelect();
        // Открыть файл
        openListener = e -> onOpen();
        // Сохранить файл
        saveListener = e -> onSave();
        // Выход
        quitListener = e -> onQuit();
        // Undo / Redo
        undoListener = e -> onUndo();
        redoListener = e -> onRedo();
        // Обработчик выхода
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                onQuit();
            }
        });
    }

    /**
     * Установка режима выбора
     */
    private void onSelect() {
        statusLabel.setText("Режим выбора");
        voyagerPanel.chooseSelectMode();
    }

    /**
     * Установка режима добавления города
     */
    private void onAddCity() {
        statusLabel.setText("Режим добавления города");
        voyagerPanel.chooseCityMode();
    }

    /**
     * Установка режима добавления пути
     */
    private void onAddWay() {
        statusLabel.setText("Режим добавления пути");
        voyagerPanel.chooseWayMode();
    }

    /**
     * Открыть файл
     */
    private void onOpen() {
        // Создаем диалог открытия
        JFileChooser fileChooser = new JFileChooser(currentFile);
        fileChooser.setCurrentDirectory(new File("."));
        // Задаем файловые фильтры
        fileChooser.addChoosableFileFilter(WorldFileFilter.getWorldFilter());
        fileChooser.addChoosableFileFilter(WorldFileFilter.getXMLFilter());
        // Показываем диалог
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            // Открываем файл
            currentFile = fileChooser.getSelectedFile();
            try {
                if (WorldFileFilter.getXMLFilter().accept(currentFile))
                    voyagerPanel.openWorldFromXML(currentFile);
                else
                    voyagerPanel.openWorldFromFile(currentFile);
                repaint();
                this.setTitle("Коммивояжер - " + currentFile.getName());
                undoManager.discardAllEdits();
                updateUndoRedoStatus();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка открытия файла: " +
                        ex.getMessage());
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Неверный формат файла: " +
                        ex.getMessage());
            } catch (JDOMException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка чтения XML: " +
                        ex.getMessage());
            }
        }
    }

    /**
     * Сохранить файл
     */
    private boolean onSave() {
        // Создаем диалог
        JFileChooser fileChooser = new JFileChooser(currentFile);
        fileChooser.setCurrentDirectory(new File("."));
        // Задаем файловый фильтр
        fileChooser.addChoosableFileFilter(WorldFileFilter.getWorldFilter());
        fileChooser.addChoosableFileFilter(WorldFileFilter.getXMLFilter());
        // Показываем диалог
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            // Сохраняем файл
            currentFile = fileChooser.getSelectedFile();
            final FileFilter currentFilter = fileChooser.getFileFilter();
            // Сохраняем в соответствии с выбранным фильтром
            if (currentFilter == WorldFileFilter.getXMLFilter()) {
                if (!WorldFileFilter.getXMLFilter().accept(currentFile)) {
                    currentFile = new File(currentFile.getPath() + ".xml");
                }
                try {
                    voyagerPanel.saveWorldToXML(currentFile);
                    this.setTitle("Коммивояжер - " + currentFile.getName());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка открытия файла: " +
                            ex.getMessage());
                }
            } else {
                if (!WorldFileFilter.getWorldFilter().accept(currentFile)) {
                    currentFile = new File(currentFile.getPath() + ".world");
                }
                try {
                    voyagerPanel.saveWorldToFile(currentFile);
                    this.setTitle("Коммивояжер - " + currentFile.getName());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка открытия файла: " +
                            ex.getMessage());
                }
            }
            undoManager.discardAllEdits();
            updateUndoRedoStatus();
            return true;
        }
        return false;
    }

    /**
     * Обработчик выхода
     */
    private void onQuit() {
        if (undoManager.canUndo()) {
            String[] vars = { "Да", "Нет", "Отмена" };
            int result = JOptionPane.showOptionDialog(this, "Файл изменён, сохранить?",
                    "", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, vars, "Да");
            if (result == JOptionPane.CANCEL_OPTION)
                return;
            if (result == JOptionPane.YES_OPTION) {
                if (!onSave()) return;
            }
            System.exit(0);
        }
        else {
            String[] vars = {"Да", "Нет"};
            int result = JOptionPane.showOptionDialog(this, "Действительно выйти?",
                    "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, vars, "Да");
            if (result == JOptionPane.YES_OPTION)
                System.exit(0);
        }
    }

    private void onUndo() {
        undoManager.undo();
        updateUndoRedoStatus();
        repaint();
    }

    private void onRedo() {
        undoManager.redo();
        updateUndoRedoStatus();
        repaint();
    }

    /**
     * Конструктор
     *
     * @param s заголовок фрейма
     */
    private VoyagerFrame(String s) {
        super(s);
        currentFile = null;
        setSize(800, 600);
        // Выбор BorderLayout
        this.setLayout(new BorderLayout());
        // Инициализация панелей
        initInfoPanel();
        initMainPanel();
        infoPanel.setListener(voyagerPanel.controller);
        voyagerPanel.setListener(infoPanel);
        initStatusBar();
        JScrollPane scrollPanel = new JScrollPane(voyagerPanel);
        scrollPanel.setMinimumSize(new Dimension(200, 200));
        scrollPanel.setPreferredSize(new Dimension(500, 500));
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPanel, infoPanel),
                BorderLayout.CENTER);
        initListeners();
        initMenuBar();
        initToolBar();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Главная функция
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VoyagerFrame("Коммивояжер"));
    }
}
