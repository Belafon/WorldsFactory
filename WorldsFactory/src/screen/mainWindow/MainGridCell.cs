using System;
using Avalonia.Controls;
using WorldsFactory.screen.panelCards;

namespace WorldsFactory.mainWindow;

/// <summary>
/// This is a node in the tree structure of cards system.
/// It can be for example <see cref="OrientedGrid"/>, which is a
/// real UserControl, Avalonias grid, which displays multiple Controls
/// in a row or column, with splitters between them,
/// or it can be a <see cref="PanelCardView"/> which holds a group of cards,
/// bud displays only one.
/// </summary>
public interface MainGridCell
{
	public OrientedGrid? getParentGrid();
	public int GetPosition();
	public void setParentGrid(OrientedGrid grid);
	public void SetPosition(int position);
}
