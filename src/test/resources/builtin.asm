	.data
sinp: .asciiz "enter > "
	.text
	.globl	main
main:
	# function main begin
	li $s0, 1
	move $t8, $s0
	li $t9, 10
	add $s0, $t8, $t9
	li $v0, 10
	syscall
