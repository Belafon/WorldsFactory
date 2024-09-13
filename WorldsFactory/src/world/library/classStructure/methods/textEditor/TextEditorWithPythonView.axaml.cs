using System.Net.Mime;
using System.Reflection.Emit;
using System.Text;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Input;
using Avalonia.Interactivity;
using Avalonia.Markup.Xaml;
using AvaloniaEdit;
using AvaloniaEdit.Document;
using AvaloniaEdit.TextMate;
using Serilog;
using TextMateSharp.Grammars;
using WorldsFactory.screen;

using System;
using Avalonia.Media;
using Avalonia.Xaml.Interactivity;
using Avalonia.VisualTree;
using Avalonia.Controls.Primitives;
using WorldsFactory.screen.panelCards;


namespace WorldsFactory.world.library.classStructure;

public partial class TextEditorWithPythonView : UserControl
{
	public Action<string> OnTextUpdated { get; set; } = (s) => { };
	public readonly TextEditor TextEditor;
	private int maxHeightInPercents;
	public TextEditorWithPythonView()
	{
		throw new NotImplementedException();
	}
	public TextEditorWithPythonView(IMethod method, int maxHeightInPercents = -1)
	{
		InitializeComponent();
		
		this.maxHeightInPercents = maxHeightInPercents;
		TextEditor = this.FindControl<TextEditor>("TextEditorWithPython")!;

		var registryOptions = new RegistryOptions(ThemeName.DarkPlus);

		var textMateInstallation = TextEditor.InstallTextMate(registryOptions);

		//getting the language by the extension and initializing grammar with this language
		textMateInstallation.SetGrammar(registryOptions.GetScopeByLanguageId(registryOptions.GetLanguageByExtension(".py").Id));
		TextEditor.Options.ShowTabs = true;
		TextEditor.Options.AllowScrollBelowDocument = true;
		var caret = TextEditor.TextArea.Caret;

		TextEditor.TextArea.TextEntered += (sender, e) =>
		{
			bool isSpecialChar = true;
			if (e.Text == "\"")
				TextEditor.Document.Insert(TextEditor.CaretOffset, "\"");
			else if (e.Text == "\'")
				TextEditor.Document.Insert(TextEditor.CaretOffset, "\'");
			else if (e.Text == "(")
				TextEditor.Document.Insert(TextEditor.CaretOffset, ")");
			else if (e.Text == "[")
				TextEditor.Document.Insert(TextEditor.CaretOffset, "]");
			else isSpecialChar = false;

			if (e.Text == "\n" && caret.Line == TextEditor.Document.LineCount)
			{
				TextEditor.ScrollToEnd();

				// find visual parent, that is a ScrollViewer
				ScrollViewer? scrollable = TextEditor.GetVisualAncestors().OfType<ScrollViewer>().FirstOrDefault();
				while (scrollable is not null)
				{
					scrollable = scrollable.GetVisualAncestors().OfType<ScrollViewer>().FirstOrDefault();
					if (scrollable is not null)
						scrollable.ScrollToEnd();
				}
			}

			if (isSpecialChar)
				TextEditor.CaretOffset--;
		};

		TextEditor.LostFocus += (sender, args) =>
		{
			OnTextUpdated(TextEditor.Text);
		};
		method.PropertyChanged += (sender, args) =>
		{
			if (args.PropertyName == "Name")
				updateMethodsHeaderInTheCode(method);
		};
		
		foreach (var parameter in method.Parameters)
		{
			parameter.PropertyChanged += (sender, args) =>
			{
				if (args.PropertyName == "Name" || args.PropertyName == "Type")
					updateMethodsHeaderInTheCode(method);
			};
		}

		method.Parameters.CollectionChanged += (sender, args) => 
		{
			if(args.NewItems is not null)
			{
				foreach (var parameter in args.NewItems)
				{
					if(parameter is Parameter param)
					{
						param.PropertyChanged += (sender, args) =>
						{
							if (args.PropertyName == "Name" || args.PropertyName == "Type")
								updateMethodsHeaderInTheCode(method);
						};
					}
				}
			}
			updateMethodsHeaderInTheCode(method);	
		};

		TextEditor.Text = method.Body!.Code;
		bool caretPositionChanged = false;
		
		
		caret.PositionChanged += (sender, args) =>
		{
			if (caretPositionChanged)
				return;
						
			caretPositionChanged = true;

			if (caret.Line == 1)
			{
				updateMethodsHeaderInTheCode(method);
				if (TextEditor.Document.LineCount <= 1)
				{
					TextEditor.AppendText("\n\t");
				}
				caret.Line = 2;
			}
			else if (caret.Line == 2 && caret.Column == 1)
			{
				if (caret.Offset >= TextEditor.Document.TextLength || TextEditor.Document.GetCharAt(caret.Offset) != '\t')
					TextEditor.Document.Insert(caret.Offset, "\t");
				caret.Column = 2;
			}

			// if the position is out of the visible area, scroll to the caset position
			var textView = TextEditor.TextArea.TextView;
			var nuberOfLineOfFirstVisible = textView.GetDocumentLineByVisualTop(textView.ScrollOffset.Y).LineNumber;
			if (caret.Line >= nuberOfLineOfFirstVisible + textView.VisualLines.Count
				|| caret.Line < nuberOfLineOfFirstVisible)
			{
				TextEditor.ScrollToLine(caret.Line);
				ScrollViewer? scrollable = TextEditor.GetVisualAncestors().OfType<ScrollViewer>().FirstOrDefault();
				while (scrollable is not null)
				{
					scrollable = scrollable.GetVisualAncestors().OfType<ScrollViewer>().FirstOrDefault();
					if (scrollable is not null)
						scrollable.Offset = new Vector(scrollable.Offset.X, TextEditor.TextArea.Caret.Line);
				}
			}
			
			caretPositionChanged = false;
		};
		
		method.Body.PropertyChanged += (sender, args) =>
		{
			if (args.PropertyName == "Code")
			{
				Task.Run(() =>
				{
					System.Threading.Thread.Sleep(100);
					Avalonia.Threading.Dispatcher.UIThread.InvokeAsync(() =>
					{
						TextEditor.Text = method.Body.Code;
					});
				});
			}
		};
	}
	

	override protected void OnAttachedToVisualTree(VisualTreeAttachmentEventArgs e)
	{
		base.OnAttachedToVisualTree(e);

		if(maxHeightInPercents != -1){
			PanelCardView? panelCardView = this.GetVisualAncestors().OfType<PanelCardView>().FirstOrDefault();
			if (panelCardView is not null)
			{
				panelCardView.CardsContentWindowGrid!.SizeChanged += (sender, args) =>
				{
					MaxHeight = panelCardView.CardsContentWindowGrid.Bounds.Height * (float)maxHeightInPercents / 100f;
				};
				MaxHeight = panelCardView.CardsContentWindowGrid.Bounds.Height * (float)maxHeightInPercents / 100f;
			}
		}
	}

	private void updateMethodsHeaderInTheCode(IMethod method)
	{
		TextDocument document = TextEditor.Document;
		// Modify the text in the first row
		document.Replace(0, document.GetLineByNumber(1).EndOffset, getMethodsHeader(method));
		TextEditor.TextArea.TextView.Redraw();
	}

	private string getMethodsHeader(IMethod method)
	{
		return MethodsBody.GetMethodsHeader(method.Name, method.Parameters.ToList());
	}
	

	public string GetText()
	{
		return TextEditor.Text;
	}
}