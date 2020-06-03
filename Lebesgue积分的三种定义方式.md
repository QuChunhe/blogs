Lebesgue积分的三种定义方式
===============================
屈春河
--------

在Riemann积分的定义中阶梯函数扮演了关键角色，即通过阶梯函数的积分（阶梯函数所围成的各个长方形面积之和）来逐渐逼近一个函数的积分。在Lebesgue积分的定义中，简单函数也扮演了类似于阶梯函数的角色。在[1]、[2]和[3]中分别通过三种方式定义Lebesgue积分，这三种定义都借助于简单函数。


**[定义1 简单函数(Simple Function)]**   
设f(x)是定义在E上的简单函数，则f(x)可以表示为   
![](http://latex.codecogs.com/gif.latex?f(x)=\sum_{i=1}^{n}\alpha_{i}\chi_{E_i}(x))   
其中，E<sub>i</sub>是可测集合并满足   
![](http://latex.codecogs.com/gif.latex?\bigcup_{i=1}^{n}E_{i}=E\text{,%20}E_{i}\cap%20E_{j}=\varnothing\text{%20}(i\neq%20j))   
![](http://latex.codecogs.com/gif.latex?\chi_{E_i}(x))
为定义在集合 E<sub>i</sub>上的指示函数。


阶梯函数的定义域E划分为有限个两两不相交的区间E<sub>1</sub>,E<sub>2</sub>,...,E<sub>n</sub>，并且在每个区间E<sub>i</sub>上的定义为常数α<sub>i</sub>。不同于阶梯函数，简单函数的定义域E可以划分为有限个两两不相交的可测集合E<sub>1</sub>,E<sub>2</sub>,...,E<sub>n</sub>,并且在每个可测集合E<sub>i</sub>的定义为常数α<sub>i</sub>。

**[定义2 简单函数的积分]**   
设(X, Ω, μ)$为可测空间，f(x)为一个可测简单函数，则f(x)的积分定义为   
![](http://latex.codecogs.com/gif.latex?\int_{E}f(x)=\sum_{i=1}^{n}\alpha_{i}\mu(E_{i})) 

如果简单函数f(x)的积分存在，即如果存在可测集合E<sub>i</sub>使得μ(E<sub>i</sub>)=∞,则在此集合上的取值α<sub>i</sub>=0,那么称f(x)是可积的或者称f(x)是一个可积的简单函数。


**[定义3 Lebesgue小和和大和]**
设可测函数f(x)定义在E上，其值在A与B之间。在[A,B]中插入分点
![](http://latex.codecogs.com/gif.latex?A=y_{0}<y_{1}<\dots<y_{n}=B)   
令![](http://latex.codecogs.com/gif.latex?e_k=\\{x:x\in%20E%20\text{%20and%20}y_{k}\le%20f(x)<y_{k+1}\\})  
则Lebesgue小和s和大和S分别定义为    
![](http://latex.codecogs.com/gif.latex?s=\sum_{k=0}^{n-1}y_{k}\mu(e_{k})\text{,}S=\sum_{k=0}^{n-1}y_{k+1}\mu(e_{k}))    
其中μ(e<sub>k</sub>)表示可测集合e<sub>k</sub>的测度。


**[定义4 可测函数的积分的定义1]**   
设f(x)为可测函数,s和S分别为其Lebesgue小和和大和，令   
![](http://latex.codecogs.com/gif.latex?U=sup\\{s\\}\text{,%20}V=inf\\{S\\})   
当![](http://latex.codecogs.com/gif.latex?\lambda=max(y_{i+1}-y_{k})\rightarrow0) 时，如果U=V，则称f(x)在E上可积，记为   
![](http://latex.codecogs.com/gif.latex?\int_{E}f(x)dx=U(\text{or}V)) 



[定义4]来自于[3]，其基于可测函数f(x),分别构造了两个特殊的和具体的简单函数序列   
![](http://latex.codecogs.com/gif.latex?h_{\lambda}(x)=\sum_{k=0}^{n-1}y_{k}\chi_{e_k}(x)\text{,%20}g_{\lambda}(x)=\sum_{k=0}^{n-1}y_{k+1}\chi_{e_{k}}(x))  
显然![](http://latex.codecogs.com/gif.latex?h_{\lambda}(x)\le\text{%20}f(x)\le\text{%20}g_{\lambda}(x)) 

Lebesgue小和s和大和S分别对应简单函数h<sub>λ</sub>(x)和g<sub>λ</sub>(x)的Lebesgue积分。随着在原有的值域区间A=y<sub>0</sub><y<sub>1</sub><...<y<sub>n</sub>=B中插入更多的分点，λ逐渐减小，并且随之h<sub>λ</sub>(x)递增，而g<sub>λ</sub>(x)递减，即通过两个简单函数序列分别从上下方向夹逼函数f(x)。如果f(x)的积分存在，则满足    
![](http://latex.codecogs.com/gif.latex?\lim_{\lambda\rightarrow\text{%20}0}h_{\lambda}(x)=\int_{E}f(x)dx=\lim_{\lambda\rightarrow\text{%20}0}g_{\lambda}(x)) 

下面将要介绍的[定义6]是在[2]中采用的定义，其没有构造特殊的、简单函数序列，而是通过满足特定条件的、抽象的简单函数序列来定义一般可测函数Lebesgue积分。

**[定义5 依平均的Cauchy序列(Cauchy sequence in the mean)]**   
一个可积简单函数序列f<sub>n</sub>，如果满足   
![](http://latex.codecogs.com/gif.latex?\int|f_n-f_m|dx\rightarrow0\text{%20when%20}m,n\rightarrow0)    
则称之为依平均的Cauchy序列


**[定义6 可测函数的积分的定义2]**   
设f(x)为广义实值可测函数，如果存在一个可积简单函数序列{f<sub>n</sub>}满足如下性质   
![](http://latex.codecogs.com/gif.latex?\text{(a)}\{f_n\}\text{%20is%20a%20Cauchy%20sequence%20in%20the%20mean})   
![](http://latex.codecogs.com/gif.latex?\text{(b)}\lim_{n\to+\infty}f_n=f(x)\text{%20a.e})   
则称f(x)可积，其积分记为    
![](http://latex.codecogs.com/gif.latex?\int_{E}f(x)dx=\lim_{n\to+\infty}\int_{E}f_{n}(x))   


显然，在[定义4]中的简单函数序列{h<sub>λ</sub>(x)}和{g<sub>λ</sub>(x)}满足在[定义6]中的条件(a)和(b)。因此，相比于[定义4]，[定义6]更具一般性。然而，为了确保[定义6]是良好定义的，需要证明如下定理。   
**[定理1]**   
如果两个函数序列{g<sub>n</sub>}和{h<sub>n</sub>}分别满足定义的条件(a)和(b)，那么   
![](http://latex.codecogs.com/gif.latex?\lim_{n\to+\infty}\int%20h_n(x)dx=\lim_{n\to+\infty}\int%20g_n(x)dx) 


[定义6]中的条件(b)可以进一步放宽为{f<sub>n</sub>}依测度收敛于f(x)，并且条件放宽之后的定义与原定义[定义6]是等价的。

**[定义7 可测函数的积分的定义2']**  
设f(x)为广义实值可测函数，如果存在一个可积简单函数序列{f<sub>n</sub>}满足如下性质   
![](http://latex.codecogs.com/gif.latex?\text{(a)}\{f_n\}\text{%20is%20a%20Cauchy%20sequence%20in%20the%20mean})   
![](http://latex.codecogs.com/gif.latex?\text{(b')}{f_{n}}\text{%20converges%20in%20measure%20to%20}f(x))    
则称f(x)可积


[1]中采用了[定义8]，其通用性更强，仅仅要求简单函数小于等于f(x)即可。


**[定义8 可测函数的积分的定义3]**   
设f(x)为定义在E上的可测函数，则f(x)在E上的积分定义为    
![](http://latex.codecogs.com/gif.latex?\\int_{E}f(x)dx=\sup_{h(x)<=f(x)}\left\\{\int_{E}h(x):h(x)\text{%20is%20a%20simple%20function}\right\\}) 


三个定义虽然形式不太一样，但是在本质上都是等价的。相对而言，[定义4]和定义[定义8]类似于Riemann积分的定义，因此比较容易理解。虽然三个定义是等价的，但是定义方式不同会造成推导Lebesgue积分的基本性质会有很大的差异。




# 参考

[1] 周民强, 实变函数论(第三版), 北京大学出版社, 2016.

[2] Avner Friedman, Foundations of Modern Analysis,Dover Publications, 2010.

[3] 那汤松, 实变函数论(第5版), 高等教育出版社, 2009.


