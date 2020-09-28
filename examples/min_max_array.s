.LC0:
	.string "\n Минимальный элемент %d\n"
.LC1:
	.string "\n Максимальный элемент %d\n"
.global main
	.text
	.type main, @function
main:
	pushq	%rbp
	movq	%rsp, %rbp
	subq	$2048, %rsp
	movl	$0,	-4(%rbp)
	movl    $77 , -8(%rbp)
	movl    $12 , -12(%rbp)
	movl    $33 , -16(%rbp)
	movl    $17 , -20(%rbp)
	movl    $28 , -24(%rbp)
	movl    $31 , -28(%rbp)
	movl    $27 , -32(%rbp)
	movl    $3 , -36(%rbp)
	movl    $6 , -40(%rbp)
	movl    $8 , -44(%rbp)
	movl	$10,	-48(%rbp)
	movl	-8(%rbp), %eax
	movl	%eax, -52(%rbp)
	movl	-8(%rbp), %eax
	movl	%eax, -56(%rbp)
	jmp	.while00
.while01:
	movl    -4(%rbp), %eax
	cltd
	movl    -44(%rbp,%rax,4), %eax
	cmpl	-52(%rbp),	%eax
	jge	.if11
	movl	-4(%rbp), %eax
	cltd
	movl	-44(%rbp,%rax,4), %eax
	movl	%eax, -52(%rbp)
.if11:
	movl	-4(%rbp), %edx
	addl	$1, %edx
	movl	%edx, -4(%rbp)
.while00:
	movl	-4(%rbp),	%eax
	cmpl	-48(%rbp),	%eax
	jl	.while01
	movl	$0,	-4(%rbp)
	jmp	.while02
.while03:
	movl    -4(%rbp), %eax
	cltd
	movl    -44(%rbp,%rax,4), %eax
	cmpl	-56(%rbp),	%eax
	jle	.if31
	movl	-4(%rbp), %eax
	cltd
	movl	-44(%rbp,%rax,4), %eax
	movl	%eax, -56(%rbp)
.if31:
	movl	-4(%rbp), %edx
	addl	$1, %edx
	movl	%edx, -4(%rbp)
.while02:
	movl	-4(%rbp),	%eax
	cmpl	-48(%rbp),	%eax
	jl	.while03
	movl	-52(%rbp),	%eax
	movl	%eax,	%esi
	movl	$.LC0,	%edi
	call	printf
	movl	-56(%rbp),	%eax
	movl	%eax,	%esi
	movl	$.LC1,	%edi
	call	printf
	leave
	ret
