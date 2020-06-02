Lebesgue积分的三种定义方式
===============================
屈春河
--------

在Riemann积分的定义中阶梯函数扮演了关键角色，即通过阶梯函数的积分（阶梯函数所围成的各个长方形面积之和）来逐渐逼近一个函数的积分。在Lebesgue积分的定义中，简单函数也扮演了类似于阶梯函数的角色。在\cite{de1}、\cite{de2}和\cite{de3}中分别通过三种方式定义Lebesgue积分，这三种定义都借助于简单函数。



**[定义1 简单函数(Simple Function)]**   
设f(x)是定义在E上的简单函数，则f(x)可以表示为   
![](http://latex.codecogs.com/gif.latex?f(x)=\sum_{i=1}^{n}\alpha_{i}\chi_{E_i}(x))

其中E<sub>i</sub>是可测集合并满足   
![](http://latex.codecogs.com/gif.latex?\bigcup_{i=1}^{n}E_i=E\text{,%20}E_i\cap%20E_j=\varnothing%20(i\neq%20j))   
![](http://latex.codecogs.com/gif.latex?\chi_{E_i}(x))
为定义在集合 E<sub>i</sub>上的指示函数。


阶梯函数的定义域E划分为有限个两两不相交的区间E<sub>1</sub>,E<sub>2</sub>,...,E<sub>n</sub>，并且在每个区间E<sub>i</sub>上的定义为常数$\alpha$<sub>i</sub>。不同于阶梯函数，简单函数的定义域E可以划分为有限个两两不相交的可测集合$E_1,E_2,\dots,E_n $,并且在每个可测集合$E_i$的定义为常数$\alpha_{i}$。

\begin{definition}[简单函数的积分]
设$(X,\Omega ,\mu)$为可测空间，$f(x)$为一个可测简单函数，则$f(x)$的积分定义为
\begin{equation}
 \int_{E}f(x)=\sum_{i=1}^{n}\alpha_{i}\mu(E_i)\nonumber
\end{equation}
\end{definition}

如果简单函数$f(x)$的积分存在，即如果存在可测集合$E_i$使得$\mu(E_i)=\infty$,则在此集合上的取值$\alpha_{i}=0$,那么称$f(x)$是可积的或者称$f(x)$是一个可积的简单函数。


\begin{definition}[Lebesgue小和和大和]
设可测函数$f(x)$定义在E上，其值在A与B之间。在$[A,B]$中插入分点
\begin{equation}
 A=y_0<y_1<\dots<y_n=B\nonumber
\end{equation}
令
\begin{equation}
 e_k=\{x:x\in E \text{ 并且 }y_k\le f(x)< y_{k+1}\}\nonumber
\end{equation}
则Lebesgue小和s和大和S分别定义为
\begin{equation}
 s=\sum_{k=0}^{n-1} y_k\mu(e_k), S=\sum_{k=0}^{n-1} y_{k+1}\mu(e_{k})\nonumber
\end{equation}
其中$\mu(e_k)$表示可测集合$e_k$的测度
\end{definition}

\begin{definition}[可测函数的积分的定义1]
\label{def1}
设$f(x)$为可测函数,s和S分别为其Lebesgue小和和大和，令
\begin{equation}
U=sup{s}, V=inf{S}\nonumber
\end{equation}
当
\begin{equation}
\lambda=max(y_{i+1}-y_k)\rightarrow 0\nonumber
\end{equation}
时，如果$U=V$，则称$f(x)$在E上可积，记为
\begin{equation}
\int_{E}f(x)dx=U(\text{或}V)\nonumber
\end{equation}
\end{definition}

定义\ref{def1}来自于\cite{de3}，其基于可测函数$f(x)$,分别构造了两个特殊的和具体的简单函数序列
\begin{equation}
h_{\lambda}(x)=\sum_{k=0}^{n-1} y_k\chi_{e_k}(x), g_{\lambda}(x)=\sum_{k=0}^{n-1} y_{k+1}\chi_{e_k}(x))\nonumber
\end{equation}
显然
\begin{equation}
h_{\lambda}(x)\le f(x)\le g_{\lambda}(x)\nonumber
\end{equation}
Lebesgue小和s和大和S分别对应简单函数$h_{\lambda}$和$g_{\lambda}(x)$的Lebesgue积分。随着在原有的值域区间$A=y_0<y_1<\dots<y_n=B$中插入更多的分点，$\lambda$逐渐减小，并且随之$h_{\lambda}$递增，而$g_{\lambda}(x)$递减，即通过两个简单函数序列分别从上下方向夹逼函数$f(x)$。如果$f(x)$的积分存在，则满足
\begin{equation}
\lim_{\lambda\rightarrow 0}h_{\lambda}(x)= \int_{E}f(x)dx=\lim_{\lambda\rightarrow 0} g_{\lambda}(x)\nonumber
\end{equation}


下面将要介绍的定义\ref{def2_1}是在\cite{de2}中采用的定义，其没有构造特殊的、简单函数序列，而是通过满足特定条件的、抽象的简单函数序列来定义一般可测函数Lebesgue积分。

\begin{definition}[依平均的Cauchy序列(Cauchy sequence in the mean)]
一个可积简单函数序列${f_n}$，如果满足
\begin{equation}
 \int|f_n - f_m|dx \rightarrow 0 \text{  当}m,n\rightarrow0\nonumber
\end{equation}
则称之为依平均的Cauchy序列
\end{definition}



\begin{definition}[可测函数的积分的定义2]
\label{def2_1}
设$f(x)$为广义实值可测函数，如果存在一个可积简单函数序列$\{f_n\}$满足如下性质
\begin{flalign}
&\text{(a) }\{f_n\}\text{为依平均的Cauchy序列} \\
&\text{(b) }\lim_{n\to +\infty}f_n=f(x) \text{ a.e}\nonumber
\end{flalign}
则称$f(x)$可积，其积分记为
\begin{equation}
\int_{E}f(x)dx=\lim_{n\to +\infty}\int_E f_{n}(x)\nonumber
\end{equation}
\end{definition}


显然，在定义\ref{def1}中的简单函数序列$h_{\lambda}$和$g_{\lambda}(x)$满足在定义\ref{def2_1}中的条件(a)和(b)。因此，相比于定义\ref{def1}，定义\ref{def2_1}更具一般性。然而，为了确保\ref{def2_1}是良好定义的，需要证明如下定理。
\begin{theorem}[]
如果两个函数序列$\{g_n\}$和$\{h_n\}$分别满足定义的条件(a)和(b)，那么
\begin{equation}
 \lim_{n\to +\infty}\int h_n(x)dx = \lim_{n\to +\infty}\int g_n(x)dx\nonumber
\end{equation}
\end{theorem}

定义\ref{def2_1}中的条件(b)可以进一步放宽为$\{f_n\}\text{依测度收敛于}f(x)$，并且条件放宽之后的定义\ref{def2_2}与原定义\ref{def2_1}是等价的。

\begin{definition}[可测函数的积分的定义2']
\label{def2_2}
设$f(x)$为广义实值可测函数，如果存在一个可积简单函数序列$\{f_n\}$满足如下性质
\begin{flalign}
&\text{(a) }\{f_n\}\text{为依平均的Cauchy序列} \\
&\text{(b') }\{f_n\}\text{依测度收敛于}f(x)\nonumber
\end{flalign}
则称$f(x)$可积
\end{definition}


\cite{de1}中采用了定义\ref{def3}，其通用性更强，仅仅要求简单函数小于等于$f(x)$即可。


\begin{definition}[可测函数的积分的定义3]
\label{def3}
设$f(x)$为定义在E上的可测函数，则f(x)在E上的积分定义为
\begin{equation}
 \int_{E}f(x)dx=\sup_{h(x)<=f(x)}\left\{ \int_{E}h(x):h(x)\text{是E上的简单函数}\right\}\nonumber
\end{equation}
\end{definition}



三个定义虽然形式不太一样，但是在本质上都是等价的。相对而言，定义\ref{def1}和定义\ref{def3}类似于Riemann积分的定义，因此比较容易理解。因为定义不同，会造成推导Lebesgue积分的基本性质会有很大的不同。



\bibliography{2020-05-31_Three_Definition_of_the_Lebesgue_Integral}



\end{document}



