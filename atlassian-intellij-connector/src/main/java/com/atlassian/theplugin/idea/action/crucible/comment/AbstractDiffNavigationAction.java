package com.atlassian.theplugin.idea.action.crucible.comment;

import com.atlassian.theplugin.commons.crucible.api.model.CommitType;
import com.atlassian.theplugin.commons.crucible.api.model.CrucibleFileInfo;
import com.atlassian.theplugin.commons.crucible.api.model.ReviewAdapter;
import com.atlassian.theplugin.idea.IdeaHelper;
import com.atlassian.theplugin.idea.crucible.CrucibleHelper;
import com.atlassian.theplugin.idea.crucible.editor.ChangeViewer;
import com.atlassian.theplugin.idea.crucible.editor.CommentHighlighter;
import com.atlassian.theplugin.idea.crucible.editor.CrucibleDiffGutterRenderer;
import com.atlassian.theplugin.idea.crucible.editor.OpenEditorDiffActionImpl;
import com.atlassian.theplugin.idea.ui.tree.AtlassianTree;
import com.atlassian.theplugin.idea.ui.tree.AtlassianTreeNode;
import com.atlassian.theplugin.idea.ui.tree.file.CrucibleFileNode;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.ex.Range;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Collection;

/**
 * User: jgorycki
 * Date: Mar 4, 2009
 * Time: 2:22:05 PM
 */
public abstract class AbstractDiffNavigationAction extends AbstractCommentAction {

	protected void selectNode(AtlassianTree tree, CrucibleFileNode node) {
		TreePath path = new TreePath(node.getPath());
		tree.scrollPathToVisible(path);
		tree.setSelectionPath(path);
	}

	protected void openFileNode(AnActionEvent e, CrucibleFileNode node, boolean goToLast) {
		Project p = IdeaHelper.getCurrentProject(e);
		if (p != null) {
			ReviewAdapter r = node.getReview();
			CrucibleFileInfo f = node.getFile();
			CrucibleHelper.openFileWithDiffs(p, true, r, f, 1, 1,
					goToLast ? new OpenEditorAndMoveToLastDiff(node, p, r, f, true)
							: new OpenEditorAndMoveToFirstDiff(node, p, r, f, true));
		}
	}

	protected Editor getEditorForNode(@NotNull CrucibleFileNode node) {
		String ourUrl = node.getFile().getFileDescriptor().getUrl();

		for (Editor editor : EditorFactory.getInstance().getAllEditors()) {
			Document document = editor.getDocument();

			VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
			if (virtualFile != null) {
				String url = virtualFile.getUserData(CommentHighlighter.CRUCIBLE_REVIEW_CONTEXT_KEY);
				if (url != null && ourUrl.equals(url)) {
					return editor;
				}
			}
		}
		return null;
	}

	protected Range getFirstRange(@NotNull Editor editor) {
		Collection<RangeHighlighter> ranges = editor.getDocument().getUserData(ChangeViewer.CRUCIBLE_RANGES);
		if (ranges != null) {
			for (RangeHighlighter rangeHighlighter : ranges) {
				CrucibleDiffGutterRenderer renderer
						= (CrucibleDiffGutterRenderer) rangeHighlighter.getLineMarkerRenderer();
				if (renderer != null) {
					return renderer.getNextRange(0);
				}
			}
		}
		return null;
	}

	protected Range getNextRange(@NotNull Editor editor) {
		Collection<RangeHighlighter> ranges = editor.getDocument().getUserData(ChangeViewer.CRUCIBLE_RANGES);
		if (ranges != null) {
			for (RangeHighlighter rangeHighlighter : ranges) {
				CrucibleDiffGutterRenderer renderer
						= (CrucibleDiffGutterRenderer) rangeHighlighter.getLineMarkerRenderer();
				if (renderer != null) {
					int offs = editor.getCaretModel().getOffset();
					int line = editor.getDocument().getLineNumber(offs);
					Range r = renderer.getNextRange(0);
					do {
						int start = r.getOffset1();
						int end = r.getOffset2();
						if (end >= line) {
							if (start > line) {
								return r;
							}
							break;
						}
						r = renderer.getNextRange(r);
					} while (r != null);
					return renderer.getNextRange(r);
				}
			}
		}
		return null;
	}

	protected Range getPrevRange(Editor editor) {
		Collection<RangeHighlighter> ranges = editor.getDocument().getUserData(ChangeViewer.CRUCIBLE_RANGES);
		if (ranges != null) {
			for (RangeHighlighter rangeHighlighter : ranges) {
				CrucibleDiffGutterRenderer renderer
						= (CrucibleDiffGutterRenderer) rangeHighlighter.getLineMarkerRenderer();
				if (renderer != null) {
					int offs = editor.getCaretModel().getOffset();
					int line = editor.getDocument().getLineNumber(offs);
					Range r = renderer.getLastRange();
					do {
						int start = r.getOffset1();
						int end = r.getOffset2();
						if (end < line) {
							if (start <= line) {
								return r;
							}
							break;
						}
						r = renderer.getPrevRange(r);
					} while (r != null);
					return renderer.getPrevRange(r);
				}
			}
		}
		return null;
	}

	protected void selectFirstDiff(@NotNull CrucibleFileNode node) {
		Editor e = getEditorForNode(node);
		if (e != null) {
			Range r = getFirstRange(e);
			selectRange(e, r);
		}
	}

	protected void selectNextDiff(@NotNull CrucibleFileNode node) {
		Editor e = getEditorForNode(node);
		if (e != null) {
			Range r = getNextRange(e);
			selectRange(e, r);
		}
	}

	protected void selectPrevDiff(CrucibleFileNode node) {
		Editor e = getEditorForNode(node);
		if (e != null) {
			Range r = getPrevRange(e);
			selectRange(e, r);
		}
	}

	private void selectRange(@NotNull Editor e, Range r) {
		if (r != null) {
			Collection<RangeHighlighter> ranges = e.getDocument().getUserData(ChangeViewer.CRUCIBLE_RANGES);
			if (ranges != null && !ranges.isEmpty()) {
				CrucibleDiffGutterRenderer renderer
						= (CrucibleDiffGutterRenderer) ranges.iterator().next().getLineMarkerRenderer();
				if (renderer != null) {
					renderer.moveToRange(r, e, e.getDocument(), false);
				}
			}
		}
	}

	protected CrucibleFileNode getNextFileNode(AtlassianTree tree, AtlassianTreeNode node, boolean alsoThis) {
		DefaultMutableTreeNode start = node;
		if (tree == null) {
			return null;
		}
		if (start == null) {
			start = (DefaultMutableTreeNode) tree.getModel().getRoot();
		}
		if (node instanceof CrucibleFileNode && alsoThis) {
			return (CrucibleFileNode) node;
		}
		AtlassianTreeNode n = (AtlassianTreeNode) start.getNextNode();
		while (n != null) {
			if (n instanceof CrucibleFileNode) {
				return (CrucibleFileNode) n;
			}
			n = (AtlassianTreeNode) n.getNextNode();
		}
		return null;
	}

	protected CrucibleFileNode getPrevFileNode(AtlassianTree tree, AtlassianTreeNode node, boolean alsoThis) {
		if (tree == null || node == null) {
			return null;
		}
		if (node instanceof CrucibleFileNode && alsoThis) {
			return (CrucibleFileNode) node;
		}
		AtlassianTreeNode n = (AtlassianTreeNode) node.getPreviousNode();
		while (n != null) {
			if (n instanceof CrucibleFileNode) {
				return (CrucibleFileNode) n;
			}
			n = (AtlassianTreeNode) n.getPreviousNode();
		}
		return null;
	}

	private class OpenEditorAndMoveToFirstDiff extends OpenEditorDiffActionImpl {
		private final CrucibleFileNode node;

		public OpenEditorAndMoveToFirstDiff(CrucibleFileNode n, Project p, ReviewAdapter r, CrucibleFileInfo f, boolean b) {
			super(p, r, f, b);
			this.node = n;
		}

		public void run(OpenFileDescriptor displayFile, VirtualFile referenceFile, CommitType commitType) {
			super.run(displayFile, referenceFile, commitType);
			selectFirstDiff(node);
		}
	}

	private class OpenEditorAndMoveToLastDiff extends OpenEditorDiffActionImpl {
		private final CrucibleFileNode node;

		public OpenEditorAndMoveToLastDiff(CrucibleFileNode n, Project p, ReviewAdapter r, CrucibleFileInfo f, boolean b) {
			super(p, r, f, b);
			this.node = n;
		}

		public void run(OpenFileDescriptor displayFile, VirtualFile referenceFile, CommitType commitType) {
			super.run(displayFile, referenceFile, commitType);
			while (getNextRange(getEditorForNode(node)) != null) {
				selectNextDiff(node);
			}
		}
	}
}