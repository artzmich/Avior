package view.tools.flowmanager;

import java.util.List;

import model.tools.flowmanager.Action;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.custom.TableEditor;

import controller.tools.flowmanager.push.ActionManagerPusher;
import controller.tools.flowmanager.table.ActionToTable;

public class ActionManager {

	protected static Shell shell;
	protected Table table_action;
	protected Combo combo;
	protected Composite composite_3;
	protected String currAction;
	protected String[][] actionTableFormat;
	final int EDITABLECOLUMN = 1;
	protected static TableEditor editor;
	protected Tree tree;
	protected List<Action> actions;

	public ActionManager() {
		open();
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		shell.dispose();
	}

	public static void displayError(String msg) {
		MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		mb.setText("Error!");
		mb.setMessage(msg);
		mb.open();
	}
	
	public static void disposeEditor(){
		// Dispose the editor do it doesn't leave a ghost table item
		if (editor.getEditor() != null) {
			editor.getEditor().dispose();
		}
	}

	// This method will populate the table with a list of the current actions
	protected void populateActionTree() {

		// Set the current action to null since the table has cleared, and a new
		// selection must be made
		currAction = null;

		// Clear the tables of any data
		table_action.removeAll();
		tree.removeAll();

		actions = StaticFlowManager.getActions();

		if (!actions.isEmpty()) {
			for (Action action : actions) {
				new TreeItem(tree, SWT.NONE).setText(action.getType());
			}
		} else {
			new TreeItem(tree, SWT.NONE).setText("None Set");
		}
	}

	// This method will populate the table with a the selected actions
	// parameters
	protected void populateActionTable(int index) {

		currAction = tree.getItem(index).getText();
		table_action.removeAll();

		for (String[] s : ActionToTable.getActionTableFormat(index)) {
			new TableItem(table_action, SWT.NO_FOCUS).setText(s);
		}
	}

	protected void setupAction(String selectedAction) {

		// Set the current action to null since
		currAction = selectedAction;

		// Clear the tables of any data
		table_action.removeAll();

		for (String[] s : ActionToTable.getNewActionTableFormat(currAction)) {
			new TableItem(table_action, SWT.NO_FOCUS).setText(s);
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(800, 350);
		if (StaticFlowManager.getFlow() != null)
			shell.setText("Action Information for Switch : "
					+ StaticFlowManager.getFlow().getSwitch() + " Flow : "
					+ StaticFlowManager.getFlow().getName());
		else
			shell.setText("Action Information for Switch : "
					+ StaticFlowManager.getCurrSwitch() + " New Flow");
		shell.setLayout(new GridLayout(2, false));

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("File");

		Menu menu_1 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_1);

		MenuItem mntmClose = new MenuItem(menu_1, SWT.NONE);
		mntmClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		mntmClose.setText("Close");

		MenuItem mntmAbout = new MenuItem(menu, SWT.CASCADE);
		mntmAbout.setText("Help");

		Menu menu_2 = new Menu(mntmAbout);
		mntmAbout.setMenu(menu_2);

		MenuItem mntmInfo = new MenuItem(menu_2, SWT.NONE);
		mntmInfo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		mntmInfo.setText("About");

		Composite composite = new Composite(shell, SWT.NONE);
		GridData gd_composite = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_composite.widthHint = 217;
		composite.setLayoutData(gd_composite);

		Button btnRefresh = new Button(composite, SWT.NONE);
		btnRefresh.setBounds(10, 0, 91, 29);
		btnRefresh.setText("Refresh");
		btnRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				populateActionTree();
			}
		});

		Label lblNewAction = new Label(composite, SWT.NONE);
		lblNewAction.setBounds(120, 5, 97, 19);
		lblNewAction.setText("New Action : ");

		Composite composite_1 = new Composite(shell, SWT.NONE);
		GridData gd_composite_1 = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_composite_1.heightHint = 39;
		gd_composite_1.widthHint = 567;
		composite_1.setLayoutData(gd_composite_1);

		combo = new Combo(composite_1, SWT.READ_ONLY);
		combo.setBounds(0, 5, 189, 29);
		combo.setItems(new String[] { "output", "enqueue", "strip-vlan",
				"set-vlan-id", "set-vlan-priority", "set-src-mac",
				"set-dst-mac", "set-tos-bits", "set-src-ip", "set-dst-ip",
				"set-src-port", "set-dst-port" });
		combo.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				disposeEditor();
				setupAction(combo.getItem(combo.getSelectionIndex()));
			}
		});

		Button btnRemoveAllActions = new Button(composite_1, SWT.NONE);
		btnRemoveAllActions.setBounds(406, 5, 151, 29);
		btnRemoveAllActions.setText("Remove All Actions");
		btnRemoveAllActions.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Remove all the actions and refresh the action tree
				ActionManagerPusher.removeAllActions();
				populateActionTree();
			}
		});

		Button btnSave = new Button(composite_1, SWT.NONE);
		btnSave.setBounds(206, 5, 91, 29);
		btnSave.setText("Save");
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (currAction != null) {
					if (!table_action.getItems()[0].getText(1).isEmpty()) {
						if (ActionToTable.errorChecksPassed(
								StaticFlowManager.getCurrSwitch(), currAction,
								table_action.getItems())) {
							ActionManagerPusher.addAction(
									table_action.getItems(), currAction);

							disposeEditor();
							populateActionTree();
						}
					} else {
						displayError("You must enter a value before you save an action!");
					}
				} else {
					displayError("You must create an action to save!");
				}
			}
		});

		Button btnRemove = new Button(composite_1, SWT.NONE);
		btnRemove.setBounds(309, 5, 91, 29);
		btnRemove.setText("Remove");
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Remove the action
				if (currAction != null) {
					ActionManagerPusher.removeAction(currAction);
					populateActionTree();
				}
			}
		});

		Composite composite_2 = new Composite(shell, SWT.NONE);
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.TOP, false, false,
				1, 1);
		gd_composite_2.heightHint = 224;
		composite_2.setLayoutData(gd_composite_2);

		tree = new Tree(composite_2, SWT.BORDER);
		tree.setBounds(0, 20, 215, 224);
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				disposeEditor();

				// Populate the action table, if we actually have actions.
				if (!tree.getSelection()[0].getText(0).equals("None Set"))
					populateActionTable(tree.indexOf(tree.getSelection()[0]));

			}
		});

		Composite composite_3 = new Composite(shell, SWT.NONE);
		GridData gd_composite_3 = new GridData(SWT.LEFT, SWT.TOP, false, false,
				1, 1);
		gd_composite_3.heightHint = 224;
		gd_composite_3.widthHint = 567;
		composite_3.setLayoutData(gd_composite_3);

		table_action = new Table(composite_3, SWT.BORDER | SWT.FULL_SELECTION);
		table_action.setBounds(0, 0, 567, 224);
		table_action.setHeaderVisible(true);
		table_action.setLinesVisible(true);

		editor = new TableEditor(table_action);
		// The editor must have the same size as the cell and must
		// not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		table_action.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				disposeEditor();

				// Identify the selected row
				TableItem item = (TableItem) e.item;
				if (item == null)
					return;

				// The control that will be the editor must be a child of the
				// Table
				Text newEditor = new Text(table_action, SWT.NONE);
				newEditor.setText(item.getText(EDITABLECOLUMN));
				newEditor.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent me) {
						Text text = (Text) editor.getEditor();
						editor.getItem()
								.setText(EDITABLECOLUMN, text.getText());
					}
				});
				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item, EDITABLECOLUMN);
			}
		});

		TableColumn tblclmnParameter = new TableColumn(table_action, SWT.NONE);
		tblclmnParameter.setWidth(200);
		tblclmnParameter.setText("Parameter");

		TableColumn tblclmnValue = new TableColumn(table_action, SWT.NONE);
		tblclmnValue.setWidth(100);
		tblclmnValue.setText("Value");

		populateActionTree();
	}
}
