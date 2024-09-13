using Avalonia.Controls;
using WorldsFactory.screen.panelCards;

namespace WorldsFactory.mainWindow;
/// <summary>
/// This is a real UserControl, Avalonias grid, which 
/// displays multiple Controls in a row or column, with 
/// splitters between them.
/// </summary>
public class OrientedGrid : Grid, MainGridCell
{
	public bool isVertical;
	private OrientedGrid? parentGrid;
	private int position;
	private MainWindow mainWindow;

	/// <summary>
	/// This is a real UserControl, Avalonias grid, which 
	/// displays multiple Controls in a row or column, with 
	/// splitters between them.
	/// </summary>
	public OrientedGrid(bool isVertical, MainWindow mainWindow)
	{
		this.isVertical = isVertical;
		this.mainWindow = mainWindow;
		this.MinWidth = 15;
		this.MinHeight = 15;
	}
	public void addCell(int position, MainGridCell cell)
	{
		cell.SetPosition(position);
		position = position * 2;

		if (position > 0)
			addGridSplitter(position - 1);

		insertChildren((Control)cell, position);

		if (position == 0 && Children.Count > 1)
			addGridSplitter(1);

		addRowOrColumn();
		cell.setParentGrid(this);
		redrawGrid();
	}
	
	/// <summary>
	/// Removes a control from the column or row
	/// at the given position.
	/// </summary>
	/// <param name="position"></param>
	/// <param name="cell"></param>
	public void removeCell(int position, MainGridCell cell)
	{
		position = position * 2;

		this.Children.RemoveAt(position);
		removeRowOrColumn();

		if (position > 0)
		{
			this.Children.RemoveAt(position - 1);
			removeRowOrColumn();
		}
		else if (this.Children.Count > 0)
		{
			this.Children.RemoveAt(0);
			removeRowOrColumn();
		}
		movePositionLaterLeft((Control)cell, position);

		if (this.Children.Count == 0)
		{
			removeItself();
			if (this == mainWindow.RootContentGrid)
				mainWindow.SetRootContent(new PanelCardView(mainWindow));
		}

		redrawGrid();
	}
	private void removeItself()
	{
		if (parentGrid is not null)
			parentGrid.removeCell(this.position, this);
	}
	private void addGridSplitter(int position)
	{
		GridSplitter splitter = new GridSplitter();
		splitter.MinWidth = 25;
		splitter.MinHeight = 25;
		splitter.ResizeDirection = GridResizeDirection.Auto;
		insertChildren(splitter, position);
		addRowOrColumn(5);
	}
	private void addRowOrColumn()
	{
		if (isVertical)
		{
			ColumnDefinition col = new ColumnDefinition();
			col.Width = new GridLength(1, GridUnitType.Star);
			this.ColumnDefinitions.Add(col);
		}
		else
		{
			RowDefinition row = new RowDefinition();
			row.Height = new GridLength(1, GridUnitType.Star);
			this.RowDefinitions.Add(row);
		}
	}
	private void removeRowOrColumn()
	{
		if (isVertical)
			this.ColumnDefinitions.RemoveAt(this.ColumnDefinitions.Count - 1);
		else this.RowDefinitions.RemoveAt(this.RowDefinitions.Count - 1);
	}
	private void addRowOrColumn(int size)
	{
		if (isVertical)
		{
			ColumnDefinition col = new ColumnDefinition();
			col.Width = new GridLength(size, GridUnitType.Pixel);
			this.ColumnDefinitions.Add(col);
		}
		else
		{
			RowDefinition row = new RowDefinition();
			row.Height = new GridLength(size, GridUnitType.Pixel);
			this.RowDefinitions.Add(row);
		}
	}
	public void redrawGrid()
	{
		if (isVertical)
			for (int i = 0; i < this.Children.Count; i++)
			{
				Control control = (Control)this.Children[i];
				Grid.SetColumn(control, i);
				Grid.SetRow(control, 0);
			}
		else
			for (int i = 0; i < this.Children.Count; i++)
			{
				Control control = (Control)this.Children[i];
				Grid.SetColumn(control, 0);
				Grid.SetRow(control, i);
			}
	}
	public OrientedGrid? getParentGrid()
	{
		return parentGrid;
	}
	public void setParentGrid(OrientedGrid parent)
	{
		this.parentGrid = parent;
	}
	
	/// <summary>
	/// Returns the position of itself in the parent row or column.
	/// </summary>
	/// <returns></returns>
	public int GetPosition()
	{
		return position;
	}
	
	/// <summary>
	/// Should be called only by the parent grid, when 
	/// the position of this changes.
	/// </summary>
	/// <param name="position"></param>
	public void SetPosition(int position)
	{
		this.position = position;
	}
	private void insertChildren(Control cell, int position)
	{
		if(position > this.Children.Count)
			position = this.Children.Count - 1;

		if(position < 0)
			position = 0;
			
		this.Children.Insert(position, cell);
		if (cell is not GridSplitter)
			for (int i = position + 1; i < this.Children.Count; i++)
				if (this.Children[i] is MainGridCell laterCell)
					laterCell.SetPosition(laterCell.GetPosition() + 1);
	}
	private void movePositionLaterLeft(Control cell, int position)
	{
		if (cell is not GridSplitter)
			for (int i = position; i < this.Children.Count; i++)
				if (this.Children[i] is MainGridCell laterCell)
					laterCell.SetPosition(laterCell.GetPosition() - 1);
	}
}
