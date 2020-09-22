.LC0:
	.string "введите а\n"
.LC1:
	.string "%d"
.LC2:
	.string "введите b\n"
.LC3:
	.string "%d"
.LC4:
	.string "НОД: %d\n"
.global main
	.text
	.type main, @function
main:
	pushq	%rbp
	movq	%rsp, %rbp
	subq	$2048, %rsp
	movl	$0,	-4(%rbp)
	movl	$0,	-8(%rbp)
	movl	$0,	-12(%rbp)
	movl	$.LC0,	%edi
	call	printf
	xorl	%eax,	%eax
	movq	$.LC1,	%rdi
	leaq	-4(%rbp),	%rsi
	call	scanf
	movl	$.LC2,	%edi
	call	printf
	xorl	%eax,	%eax
	movq	$.LC3,	%rdi
	leaq	-8(%rbp),	%rsi
	call	scanf
	jmp	.while00
.while01:
	movl	-4(%rbp),	%eax
	cmpl	-8(%rbp),	%eax
	jle	.if11
	movl	-4(%rbp), %eax
	movl	%eax,	-12(%rbp)
	movl	-8(%rbp), %eax
	movl	%eax,	-4(%rbp)
	movl	-12(%rbp), %eax
	movl	%eax,	-8(%rbp)
.if11:
	movl	-8(%rbp), %edx
	subl	-4(%rbp), %edx
	movl	%edx, -8(%rbp)
.while00:
	movl	-4(%rbp),	%eax
	cmpl	-8(%rbp),	%eax
	jne	.while01
	movl	-4(%rbp),	%eax
	movl	%eax,	%esi
	movl	$.LC4,	%edi
	call	printf
	leave
	ret
