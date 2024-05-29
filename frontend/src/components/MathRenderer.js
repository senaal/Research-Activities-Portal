import React from 'react';
import { MathJax, MathJaxContext } from 'better-react-mathjax';
import { BlockMath, InlineMath } from 'react-katex';
import 'katex/dist/katex.min.css';

const MathRenderer = ({ content }) => {
  if (!content) return null;

  const isMathML = content.includes('<math') || content.includes('<mml:math');
  const isLaTeX = content.includes('\\begin{document}') || content.includes('$$') || content.includes('$') || content.includes('\\') || content.includes('\\usepackage');

  if (isMathML) {
    return (
      <MathJaxContext>
        <MathJax>
          <div dangerouslySetInnerHTML={{ __html: content }} />
        </MathJax>
      </MathJaxContext>
    );
  }

  if (isLaTeX) { 
    const cleanedContent = content
      .replace(/\\documentclass\[.*?\]\{.*?\}.*?\\begin\{document\}/gs, '')
      .replace(/\\end\{document\}/gs, '')
      .replace(/\\usepackage\{.*?\}/g, '')
      .replace(/\\setlength\{.*?\}\{.*?\}/g, '');

    // Split content by inline and block LaTeX delimiters
    const parts = cleanedContent.split(/(\$\$.*?\$\$|\$.*?\$)/g).map((part, i) => {
      if (part.startsWith('$$') && part.endsWith('$$')) {
        return <BlockMath key={i} math={part.slice(2, -2)} />;
      } else if (part.startsWith('$') && part.endsWith('$')) {
        return <InlineMath key={i} math={part.slice(1, -1)} />;
      } else {
        return <span key={i}>{part}</span>;
      }
    });

    return <>{parts}</>;
  }

  return <span>{content}</span>;
};

export default MathRenderer;
