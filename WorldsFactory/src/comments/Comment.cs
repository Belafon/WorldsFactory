using System;

namespace WorldsFactory.comments;

public abstract class Comment
{
	private string text = "";
	public void setText(string text)
	{
		// TODO: check all windows and all panels to find if some panel should not change
		this.text = text;
	}
	public string getText()
	{
		return text;
	}
}
