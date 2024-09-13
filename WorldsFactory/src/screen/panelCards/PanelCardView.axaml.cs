using System;
using System.Collections.Generic;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Controls.Shapes;
using Avalonia.Input;
using Avalonia.Interactivity;
using Avalonia.Markup.Xaml;
using Avalonia.Media;
using Avalonia.Styling;
using NP.Utilities;
using WorldsFactory.mainWindow;
using WorldsFactory.screen.panelCards.cards;

namespace WorldsFactory.screen.panelCards;

/// <summary>
/// Represents a single place for a card in the card system.
/// It holds a list of cards with a tab list, which allows to 
/// switch between them.
/// It displays a content of the currently focused card.
/// </summary>
internal partial class PanelCardView : UserControl, MainGridCell
{
	public Grid? CardsContentWindowGrid { get; set; }

    private const int MinWidthOfPanel = 15;
    private const int MinHeightOfPanel = 15;
    private MainWindow? mainWindow;
	private Canvas? canvas;
	private ListBox? tabList;
	private static PanelCardView? lastFocusedPanelCardViewWhileDragging;
	internal PanelCardViewModel Model { get; set; }
	private OrientedGrid? parentGrid;
	private int position;
	private bool isRemoved = false;

	/// <summary>
	/// Represents a single place for a card in the card system.
	/// It holds a list of cards with a tab list, which allows to 
	/// switch between them.
	/// It displays a content of the currently focused card.
	/// </summary>
	public PanelCardView()
	{
		throw new NotImplementedException();
	}

	/// <summary>
	/// Represents a single place for a card in the card system.
	/// It holds a list of cards with a tab list, which allows to 
	/// switch between them.
	/// It displays a content of the currently focused card.
	/// </summary>
	public PanelCardView(MainWindow mainWindow)
	{
		this.mainWindow = mainWindow;
		DataContext = Model = new PanelCardViewModel(mainWindow, this);
		InitializeComponent();
		initFindControl();
		this.MinWidth = MinWidthOfPanel;
		this.MinHeight = MinHeightOfPanel;
	}

	/// <summary>
	/// Represents a single place for a card in the card system.
	/// It holds a list of cards with a tab list, which allows to 
	/// switch between them.
	/// It displays a content of the currently focused card.
	/// </summary>
	public PanelCardView(MainWindow mainWindow, Card card)
	{
		this.mainWindow = mainWindow;
		DataContext = Model = new PanelCardViewModel(mainWindow, card, this);
		InitializeComponent();
		initFindControl();
		this.MinWidth = MinWidthOfPanel;
		this.MinHeight = MinHeightOfPanel;
	}

	/// <summary>
	/// Represents a single place for a card in the card system.
	/// It holds a list of cards with a tab list, which allows to 
	/// switch between them.
	/// It displays a content of the currently focused card.
	/// </summary>
	public PanelCardView(UserControl cardBody, string name, MainWindow mainWindow)
	{
		this.mainWindow = mainWindow;
		DataContext = Model = new PanelCardViewModel(cardBody, name, mainWindow, this);
		InitializeComponent();
		initFindControl();
		this.MinWidth = MinWidthOfPanel;
		this.MinHeight = MinHeightOfPanel;
		/* if(cardBody.Parent is not null)
			((Panel)cardBody.Parent).Children.Remove(cardBody);    */
	}
	private void InitializeComponent()
	{
		AvaloniaXamlLoader.Load(this);
	}
	public OrientedGrid getParentGrid()
	{
		return parentGrid!;
	}
	public void setParentGrid(OrientedGrid parent)
	{
		this.parentGrid = parent;
	}
	public int GetPosition()
	{
		return position;
	}
	public void SetPosition(int position)
	{
		this.position = position;
	}
	
	/// <summary>
	/// Splits the space of PanelCardView to two PanelCardViews.
	/// </summary>
	/// <param name="panel">New PanelCardView, which fills the free space after split.</param>
	/// <param name="side">Side of the panel</param>
	public void SplitPanel(PanelCardView panel, Side side)
	{
		if (this.parentGrid is not null)
		{
			if (side == Side.Left && parentGrid.isVertical)
				parentGrid.addCell(position, panel);
			else if (side == Side.Right && parentGrid.isVertical)
				parentGrid.addCell(position + 1, panel);
			else if ((side == Side.Top || side == Side.Bottom) && parentGrid.isVertical)
				this.wrapWithNewGrid(panel, side);
			else if (side == Side.Top && !parentGrid.isVertical)
				parentGrid.addCell(position, panel);
			else if (side == Side.Bottom && !parentGrid.isVertical)
				parentGrid.addCell(position + 1, panel);
			else if ((side == Side.Left || side == Side.Right) && !parentGrid.isVertical)
				this.wrapWithNewGrid(panel, side);
		}
		else this.wrapWithNewGrid(panel, side);

	}
	private void wrapWithNewGrid(PanelCardView panel, Side side)
	{
		OrientedGrid grid;
		if (side == Side.Top || side == Side.Bottom)
			grid = new OrientedGrid(false, mainWindow!);
		else grid = new OrientedGrid(true, mainWindow!);
		grid.SetPosition(this.position);
		if (this.parentGrid is not null)
		{
			OrientedGrid rootParentGrid = this.parentGrid;
			this.parentGrid.Children[this.position * 2] = grid;
			grid.setParentGrid(this.parentGrid);
			rootParentGrid.redrawGrid();
		}
		else mainWindow!.SetRootContent(grid);

		if (panel.parentGrid is not null)
			panel.parentGrid.removeCell(panel.position, this);

		if (side == Side.Right || side == Side.Bottom)
		{
			grid.addCell(0, this);
			grid.addCell(1, panel);
		}
		else
		{
			grid.addCell(0, panel);
			grid.addCell(1, this);
		}
	}

	private void initFindControl()
	{
		CardsContentWindowGrid = this.FindControl<Grid>("CardsContentWindow")!;
		canvas = this.FindControl<Canvas>("PanelCardsCanvas")!;
		tabList = this.FindControl<ListBox>("tabs")!;
		GotFocus += (sender, e) => onFocus();
		onFocus();
	}
	private void onFocus()
	{
		if (Model.FocusedCard is not null
			&& mainWindow is not null
			&& (mainWindow.FocusCardHistory.Count == 0 ||
				mainWindow.FocusCardHistory.Peek() != Model.FocusedCard))
		{
			mainWindow.FocusCardHistory.Push(Model.FocusedCard!);
		}
	}
	public void AddCard(UserControl card, string name)
	{
		if (this.DataContext is PanelCardViewModel model)
		{
			model.AppendCard(card, name);
		}
		else throw new TypeAccessException("PanelCardView: addCard, DataContext is not PanelCardViewModel");
	}
	public void AddEmptyCard()
	{
		if (this.DataContext is PanelCardViewModel model)
		{
			model.AddEmptyCard();
		}
		else throw new TypeAccessException("PanelCardView: addEmptyCard, DataContext is not PanelCardViewModel");
	}
	public void RemoveItself()
	{
		if (isRemoved)
			return;
		isRemoved = true;

		mainWindow?.Containers.Remove(Model);

		if (parentGrid is not null)
			parentGrid.removeCell(position, this);
		else mainWindow!.SetRootContent(new PanelCardView(mainWindow));
	}



	// ------ Methods for handling dragging cards ------ 



	private static Border? coloredTabDraggingBorder;
	public void OnPointerMovedOverTabWhileCardDragged(DragEventArgs e, Control panelCard, Card card, StackPanel tab)
	{
		double x = e.GetPosition(tab).X;

		if (DataContext is PanelCardViewModel context)
		{
			int cardPosition = context.cards.IndexOf(card);
			resetlastFocusedPanelCardViewWhileDragging();
			if (cardPosition != -1)
			{
				if (coloredTabDraggingBorder is not null)
					coloredTabDraggingBorder.Padding = new Thickness(0, 0, 0, 0);

				if (tab.Bounds.Width / 1.8 - x > 0)
				{
					if (tab.Children[0] is Border leftBorder)
					{
						leftBorder.Padding = new Thickness(5, 0, 0, 0);
						coloredTabDraggingBorder = leftBorder;
					}
					if (tab.Children[4] is Border rightBorder)
					{
						rightBorder.Padding = new Thickness(0, 0, 0, 0);
					}
				}
				else
				{
					if (tab.Children[0] is Border leftBorder)
					{
						leftBorder.Padding = new Thickness(0, 0, 0, 0);
					}
					if (tab.Children[4] is Border rightBorder)
					{
						rightBorder.Padding = new Thickness(5, 0, 0, 0);
						coloredTabDraggingBorder = rightBorder;

					}
				}
			}
		}
	}
	
	/// <summary>
	/// Resets colored tab, that was colored because of dragging a card over it, 
	/// Resets effects of dragging a card over a card.
	/// </summary>
	private void resetDraggingEffects()
	{
		resetColoredTabDraggingBorder();
		resetlastFocusedPanelCardViewWhileDragging();
	}
	private void resetColoredTabDraggingBorder()
	{
		if (coloredTabDraggingBorder is not null)
		{
			coloredTabDraggingBorder.Padding = new Thickness(0, 0, 0, 0);
			coloredTabDraggingBorder = null;
		}
	}
	/// <summary>
	/// Removes the colored rectangle, that was created while dragging a card over a card.
	/// </summary>
	private void resetlastFocusedPanelCardViewWhileDragging()
	{
		if (lastFocusedPanelCardViewWhileDragging is not null)
		{
			lastFocusedPanelCardViewWhileDragging.canvas!.Children.Clear();
			lastFocusedPanelCardViewWhileDragging.lastPotentionallySplitting = LastRectHintSplitCondition.idle;
			lastFocusedPanelCardViewWhileDragging = null;
		}
	}
	public void UpdateOnPointerReleasedOverTabWhileCardDragged(DragEventArgs e, Control panelCard, Card targetCard, StackPanel tab)
	{
		double x = e.GetPosition(tab).X;

		resetDraggingEffects();

		if (DataContext is PanelCardViewModel context)
		{
			var data = e.Data.Get("Card");
			if (data is not null && data is Card draggedCard)
			{
				if ((context.cards.Contains(draggedCard)
					&& context.cards.Count == 1)
					|| draggedCard == targetCard)
					return;

				draggedCard.CloseItselfForAWhile();
				int cardPosition = context.cards.IndexOf(targetCard);

				if (cardPosition != -1)
				{

					if (tab.Bounds.Width / 1.8 - x > 0)
					{
						context.AddCard(draggedCard, cardPosition);
					}
					else
					{
						context.AddCard(draggedCard, cardPosition + 1);
					}
				}
			}
		}
	}
	public void UpdateOnPointerReleasedOverCardWhileCardDragged(DragEventArgs e, Control cardBody)
	{
		Tuple<bool, bool> part = isPointerSplittingVertically(
			e.GetPosition(cardBody).X,
			e.GetPosition(cardBody).Y, cardBody);

		resetDraggingEffects();

		var data = e.Data.Get("Card");

		if (data is not null && data is Card card)
		{
			if (DataContext is PanelCardViewModel model)
			{
				if (model.cards.Contains(card)
					&& model.cards.Count < 2) // avoid moving card to the same panel with only one child
					return;

				card.CloseItselfForAWhile();
				if (part.Item1 && part.Item2)
					model.AddCard(card, Side.Left);
				else if (part.Item1 && !part.Item2)
					model.AddCard(card, Side.Right);
				else if (!part.Item1 && part.Item2)
					model.AddCard(card, Side.Top);
				else if (!part.Item1 && !part.Item2)
					model.AddCard(card, Side.Bottom);
			}
			else throw new TypeAccessException("PanelCardView: OnPointerReleasedOverCardWhileCardDragged, DataContext is not PanelCardViewModel");

		}

		lastPotentionallySplitting = LastRectHintSplitCondition.idle;
	}
	public void UpdateOnPointerReleasedWhileCardDraggedOutOfTheTab()
	{
		resetDraggingEffects();
	}
	public enum LastRectHintSplitCondition
	{
		idle,
		vertical,
		horizontal
	}
	public LastRectHintSplitCondition lastPotentionallySplitting = LastRectHintSplitCondition.idle;
	public void UpadteOnPointerMovedOverCardWhileCardDragged(DragEventArgs e, Control cardBody)
	{
		Tuple<bool, bool> part = isPointerSplittingVertically(
			e.GetPosition(cardBody).X,
			e.GetPosition(cardBody).Y, cardBody);

		resetColoredTabDraggingBorder();

		var rectangle = new Rectangle
		{
			Name = "cardSplittingRectangle",
			Fill = Brush.Parse("#aa000000")
		};

		if ((part.Item1 && lastPotentionallySplitting != LastRectHintSplitCondition.vertical)
			|| (lastPotentionallySplitting == LastRectHintSplitCondition.idle && part.Item1))
		{ // lets go vertical slice


			clearCanvasFromLastPanelCardInDraggingState();

			canvas!.Children.Clear();
			rectangle.Width = CardsContentWindowGrid!.Bounds.Width / 2;
			rectangle.Height = CardsContentWindowGrid!.Bounds.Height;

			if (part.Item2)
			{ // is left
				Canvas.SetTop(rectangle, 0);
				Canvas.SetLeft(rectangle, 0);
			}
			else
			{ // is right
				Canvas.SetTop(rectangle, 0);
				Canvas.SetLeft(rectangle, CardsContentWindowGrid!.Bounds.Width / 2);
			}

			lastPotentionallySplitting = LastRectHintSplitCondition.vertical;
			canvas!.Children.Add(rectangle);

		}
		else if ((lastPotentionallySplitting == LastRectHintSplitCondition.idle && !part.Item1)
		|| (!part.Item1 && lastPotentionallySplitting != LastRectHintSplitCondition.horizontal))
		{ // horizontal slice

			clearCanvasFromLastPanelCardInDraggingState();

			canvas!.Children.Clear();
			rectangle.Width = CardsContentWindowGrid!.Bounds.Width;
			rectangle.Height = CardsContentWindowGrid!.Bounds.Height / 2;

			if (part.Item2)
			{ // is top
				Canvas.SetTop(rectangle, 0);
				Canvas.SetLeft(rectangle, 0);
			}
			else
			{ // is bottom
				Canvas.SetTop(rectangle, CardsContentWindowGrid!.Bounds.Height / 2);
				Canvas.SetLeft(rectangle, 0);
			}

			lastPotentionallySplitting = LastRectHintSplitCondition.horizontal;
			canvas.Children.Add(rectangle);
		}
	}
	private void clearCanvasFromLastPanelCardInDraggingState()
	{
		if (lastFocusedPanelCardViewWhileDragging is not null)
		{
			lastFocusedPanelCardViewWhileDragging.canvas!.Children.Clear();
			lastFocusedPanelCardViewWhileDragging.lastPotentionallySplitting = LastRectHintSplitCondition.idle;
		}
		lastFocusedPanelCardViewWhileDragging = this;
	}
	public void UpdateOnPointerLeaveWhileCardDragged()
	{
		resetDraggingEffects();
		if(canvas is not null)
			canvas.Children.Clear();
	}
	private DockPanel? findTabITem(Control control)
	{
		if (control is DockPanel item
			&& item.Name == "tabItem")
			return item;
		if (control.Parent is null)
			return null;
		return findTabITem((Control)control.Parent);
	}
	private Tuple<bool, bool> isPointerSplittingVertically(double x, double y, Control container)
	{
		double funcIncresingDiagonalY = x * (CardsContentWindowGrid!.Bounds.Bottom / CardsContentWindowGrid!.Bounds.Width);
		double funcDecresingDiagonalY = CardsContentWindowGrid!.Bounds.Bottom - (x * (CardsContentWindowGrid!.Bounds.Bottom / CardsContentWindowGrid!.Bounds.Width));
		if (funcIncresingDiagonalY < y && funcDecresingDiagonalY < y)
			return new Tuple<bool, bool>(false, false);
		else if (funcIncresingDiagonalY > y && funcDecresingDiagonalY > y)
			return new Tuple<bool, bool>(false, true);
		else if (funcIncresingDiagonalY > y && funcDecresingDiagonalY < y)
			return new Tuple<bool, bool>(true, false);
		else return new Tuple<bool, bool>(true, true);
	}
}