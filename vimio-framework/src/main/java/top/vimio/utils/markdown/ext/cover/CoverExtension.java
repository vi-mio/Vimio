package top.vimio.utils.markdown.ext.cover;

import org.commonmark.Extension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentNodeRendererContext;
import org.commonmark.renderer.text.TextContentNodeRendererFactory;
import org.commonmark.renderer.text.TextContentRenderer;
import top.vimio.utils.markdown.ext.cover.internal.CoverDelimiterProcessor;
import top.vimio.utils.markdown.ext.cover.internal.CoverHtmlNodeRenderer;
import top.vimio.utils.markdown.ext.cover.internal.CoverTextContentNodeRenderer;

/**
 * @Description: 自定义遮盖层拓展
 * @Author: Vimio
 * @Date: 2024/8/28 14:08
 */
public class CoverExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension, TextContentRenderer.TextContentRendererExtension {
	private CoverExtension() {
	}

	public static Extension create() {
		return new CoverExtension();
	}

	@Override
	public void extend(Parser.Builder parserBuilder) {
		parserBuilder.customDelimiterProcessor(new CoverDelimiterProcessor());
	}

	@Override
	public void extend(HtmlRenderer.Builder rendererBuilder) {
		rendererBuilder.nodeRendererFactory(new HtmlNodeRendererFactory() {
			@Override
			public NodeRenderer create(HtmlNodeRendererContext context) {
				return new CoverHtmlNodeRenderer(context);
			}
		});
	}

	@Override
	public void extend(TextContentRenderer.Builder rendererBuilder) {
		rendererBuilder.nodeRendererFactory(new TextContentNodeRendererFactory() {
			@Override
			public NodeRenderer create(TextContentNodeRendererContext context) {
				return new CoverTextContentNodeRenderer(context);
			}
		});
	}
}
