/*
 * User: eldad.Dor
 * Date: 17/08/2014 15:43
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.inspector.gadget.config;

import com.idi.intellij.plugin.inspector.gadget.model.InsepctorSettingsConfiguration;
import com.idi.intellij.plugin.inspector.gadget.model.InspectorSettings;
import com.intellij.ide.util.TreeJavaClassChooserDialog;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.util.ClassUtil;
import com.intellij.ui.components.FixedColumnsModel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.ui.UIUtil;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.combobox.MapComboBoxModel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Set;

/**
 * @author eldad
 * @date 17/08/2014
 */
public class InspectorConfigView extends SearchableConfigurable.Parent.Abstract {
	private static final Logger logger = Logger.getInstance(InspectorConfigView.class.getName());
	public static final String INSPECTOR_GADGET_ID = "InspectorGadget";

	private JList annotationsList;
	private JPanel inspectorSettingsPanel;
	private JButton btnRemoveAnnotation;
	private JButton btnAddAnnotation;
	private JTextField txtAddNewAnnotation;
	private JXHyperlink hyperLinkEditAnnotation;
	private JXHyperlink hyperLinkSaveAnnotation;
	private JPanel astroTestsPanel;
	private JPanel astroTestPanel;
	private JTable astroTestClassTbl;
	private InspectorSettings settings;
	private final Project project;
	private boolean isModified;
	private Integer selectedIndex;

	private static final String CANCEL_EDIT = "Cancel Edit";
	private static final String EDIT_ANNOTATION_FQN = "Edit Annotation FQN";

	@Override
	protected Configurable[] buildConfigurables() {
		return new InspectorConfigView[]{this};
	}

	@NotNull
	@Override
	public String getId() {
		return INSPECTOR_GADGET_ID;
	}

	@Nls
	@Override
	public String getDisplayName() {
		return INSPECTOR_GADGET_ID;
	}

	@Nullable
	@Override
	public String getHelpTopic() {
		return getId();
	}

	@Override
	public boolean isVisible() {
		return true;
	}


	@Override
	public void apply() throws ConfigurationException {
		if (isModified) {
			ServiceManager.getService(project, InsepctorSettingsConfiguration.class).loadState(settings);
		}
		isModified = false;
	}

	@Override
	public void reset() {
		super.reset();
	}


	public InspectorConfigView(Project project) {
		logger.info("InspectorConfigView():");
		this.project = project;
		settings = ServiceManager.getService(project, InsepctorSettingsConfiguration.class).getState();
		initializeListeners();
		populateListFromSettings();

	}

	private void populateListFromSettings() {
		DefaultListModel model = new DefaultListModel();
		final Set<String> annotationsKeys = settings.ANNOTATIONS.keySet();
		for (String annotationsKey : annotationsKeys) {
			model.addElement(settings.ANNOTATIONS.get(annotationsKey) + "." + annotationsKey);
		}
		annotationsList.setModel(model);
//		txtAstroTestClass.setText(settings.ASTRO_TEST_CLASS);
//		txtIFSTestClass.setText(settings.IFS_TEST_CLASS);
		populateAstroTestTable();
	}



	private void populateAstroTestTable() {
		final Map<String, String> annotations = settings.ANNOTATIONS;
		final MapComboBoxModel<String, String> boxModel = new MapComboBoxModel<String, String>(annotations);

		final FixedColumnsModel model = new FixedColumnsModel(annotationsList.getModel(), 5);
		annotationsList.setModel(boxModel);
	}

	private void initializeListeners() {
		annotationsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (!event.getValueIsAdjusting()) {
					JList source = (JList) event.getSource();
					if (source.getSelectedValue() != null) {
						String selected = source.getSelectedValue().toString();
						selectedIndex = source.getSelectedIndex();
						hyperLinkEditAnnotation.setVisible(true);
					}
				}
			}
		});
		btnAddAnnotation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (saveAnnotationFQN()) {
					return;
				}
				if (settings.equals(ServiceManager.getService(project, InsepctorSettingsConfiguration.class).getState())) {
					logger.info("actionPerformed():");
				}
			}
		});
		btnRemoveAnnotation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedIndex != null) {
					final String selectedFQN = String.valueOf(annotationsList.getModel().getElementAt(selectedIndex));
					Messages.showOkCancelDialog("Are you sure you want to remove inspection for the Annotation FQN=\n" + selectedFQN, "Remove Annotation FQN for Inspection", Messages.getWarningIcon());
					annotationsList.remove(selectedIndex);
				}
			}
		});
		hyperLinkEditAnnotation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedIndex != null) {
					if (e.getActionCommand().equals(EDIT_ANNOTATION_FQN)) {
						if (txtAddNewAnnotation.getText().isEmpty()) {
							txtAddNewAnnotation.setText(String.valueOf(annotationsList.getModel().getElementAt(annotationsList.getSelectedIndex())));
							annotationsList.setEnabled(false);
							hyperLinkEditAnnotation.setText(CANCEL_EDIT);
							hyperLinkSaveAnnotation.setVisible(true);
							btnAddAnnotation.setEnabled(false);
							btnRemoveAnnotation.setEnabled(false);
						}
					}
					if (e.getActionCommand().equals(CANCEL_EDIT)) {

					}
				}
			}
		});
		hyperLinkSaveAnnotation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (saveAnnotationFQN()) {
					annotationsList.clearSelection();
					txtAddNewAnnotation.setText("");

					hyperLinkEditAnnotation.setText(EDIT_ANNOTATION_FQN);
					hyperLinkSaveAnnotation.setVisible(false);
					hyperLinkEditAnnotation.setVisible(false);
					annotationsList.setEnabled(true);
					btnAddAnnotation.setEnabled(true);
					btnRemoveAnnotation.setEnabled(true);
				}
			}
		});
	/*	txtIFSTestClass.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changingAstroTestFQN("Changing IFS Test FQN!\nOnly make changes if you're completely aware to the consequences!", txtIFSTestClass);
			}
		});
		txtAstroTestClass.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changingAstroTestFQN("Changing ASTRO Test FQN!\nOnly make changes if you're completely aware to the consequences!", txtAstroTestClass);
			}
		});*/
	}

	private void changingAstroTestFQN(final String message, JTextField textField) {
		final TreeJavaClassChooserDialog chooserDialog = new TreeJavaClassChooserDialog("Selected A TestClass", project);
		chooserDialog.showDialog();
		final PsiClass selected = chooserDialog.getSelected();
		if (selected != null && !textField.getText().equalsIgnoreCase(selected.getQualifiedName())) {
			UIUtil.invokeAndWaitIfNeeded(new Runnable() {
				@Override
				public void run() {
					Messages.showOkCancelDialog(message, "Astro Test Class", Messages.getWarningIcon());
				}
			});
		}
	}

	private boolean saveAnnotationFQN() {
		if (!txtAddNewAnnotation.getText().isEmpty()) {
			final String classFQN = txtAddNewAnnotation.getText().trim();
			final PsiClass psiClass = ClassUtil.findPsiClassByJVMName(PsiManager.getInstance(project), classFQN);
			if (psiClass == null) {
				showMessageDialog("Annotation with FQN: " + classFQN + " wasn't found in project", Messages.getErrorIcon());
				return true;
			}
			final PsiPackage psiPackage = JavaPsiFacade.getInstance(project).findPackage(classFQN.substring(0, psiClass.getQualifiedName().lastIndexOf(".")));
			if (psiClass != null && psiClass.isValid()) {
				settings.ANNOTATIONS.put(psiClass.getName(), psiPackage.getName());
				isModified = true;
				return true;
			}
		}
		return false;
	}


	private void showMessageDialog(final String message, final Icon messageIcon) {
		UIUtil.invokeAndWaitIfNeeded(new Runnable() {
			@Override
			public void run() {
				Messages.showMessageDialog(message, "Class Not Found!", messageIcon);
			}
		});
	}


	@Override
	public JComponent createComponent() {
		return inspectorSettingsPanel;
	}

	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		inspectorSettingsPanel = new JPanel();
		inspectorSettingsPanel.setLayout(new GridLayoutManager(11, 15, new Insets(0, 0, 0, 0), -1, -1));
		final Spacer spacer1 = new Spacer();
		inspectorSettingsPanel.add(spacer1, new GridConstraints(1, 14, 5, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setText("Annotations FQN:");
		inspectorSettingsPanel.add(label1, new GridConstraints(0, 0, 1, 15, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer2 = new Spacer();
		inspectorSettingsPanel.add(spacer2, new GridConstraints(7, 13, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final Spacer spacer3 = new Spacer();
		inspectorSettingsPanel.add(spacer3, new GridConstraints(6, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JScrollPane scrollPane1 = new JScrollPane();
		inspectorSettingsPanel.add(scrollPane1, new GridConstraints(1, 0, 2, 8, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(500, -1), new Dimension(500, -1), new Dimension(500, -1), 0, false));
		annotationsList = new JList();
		scrollPane1.setViewportView(annotationsList);
		btnAddAnnotation = new JButton();
		btnAddAnnotation.setText("Add");
		inspectorSettingsPanel.add(btnAddAnnotation, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("Add new Annotation to inspect:");
		inspectorSettingsPanel.add(label2, new GridConstraints(3, 0, 1, 10, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		txtAddNewAnnotation = new JTextField();
		inspectorSettingsPanel.add(txtAddNewAnnotation, new GridConstraints(4, 0, 1, 12, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		btnRemoveAnnotation = new JButton();
		btnRemoveAnnotation.setText("Remove");
		inspectorSettingsPanel.add(btnRemoveAnnotation, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		hyperLinkEditAnnotation = new JXHyperlink();
		hyperLinkEditAnnotation.setText("Edit Annotation FQN");
		hyperLinkEditAnnotation.setVisible(false);
		inspectorSettingsPanel.add(hyperLinkEditAnnotation, new GridConstraints(5, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		hyperLinkSaveAnnotation = new JXHyperlink();
		hyperLinkSaveAnnotation.setText("Save Annotation FQN");
		hyperLinkSaveAnnotation.setVisible(false);
		inspectorSettingsPanel.add(hyperLinkSaveAnnotation, new GridConstraints(5, 4, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		astroTestsPanel = new JPanel();
		astroTestsPanel.setLayout(new GridBagLayout());
		inspectorSettingsPanel.add(astroTestsPanel, new GridConstraints(6, 0, 4, 7, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, new Dimension(-1, 100), 0, false));
		astroTestsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null));
		final JLabel label3 = new JLabel();
		label3.setText("Astro Test Classes:");
		GridBagConstraints gbc;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		astroTestsPanel.add(label3, gbc);
		final JLabel label4 = new JLabel();
		label4.setIcon(new ImageIcon(getClass().getResource("/images/AstroLogo_48_2.png")));
		label4.setText("");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridheight = 2;
		gbc.weighty = 1.0;
		astroTestsPanel.add(label4, gbc);
		astroTestPanel = new JPanel();
		astroTestPanel.setLayout(new FormLayout("fill:d:grow", "center:d:grow"));
		astroTestPanel.setMinimumSize(new Dimension(300, 21));
		astroTestPanel.setPreferredSize(new Dimension(500, 21));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 4;
		gbc.fill = GridBagConstraints.BOTH;
		astroTestsPanel.add(astroTestPanel, gbc);
		astroTestClassTbl = new JTable();
		astroTestClassTbl.setMinimumSize(new Dimension(200, 32));
		astroTestClassTbl.setPreferredSize(new Dimension(400, 32));
		CellConstraints cc = new CellConstraints();
		astroTestPanel.add(astroTestClassTbl, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.FILL));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return inspectorSettingsPanel;
	}
}