	.data
sinp: .asciiz "enter > "
	.text
	.globl	main
main:
	# function main begin
	# function call: one()
	jal one
	move $a0, $v0
	li $v0, 1
	syscall
	li $v0, 10
	syscall
one:
	# function one load
	addi $sp, $sp, -4
	sw $ra, 0($sp)
	addi $sp, $sp, -4
	sw $s0, 0($sp)
	# function one begin
	# function call: two()
	jal two
	move $s0, $v0
	move $v0, $s0
	# function one unload
	lw $s0, 0($sp)
	addi $sp, $sp, 4
	lw $ra, 0($sp)
	addi $sp, $sp, 4
	jr $ra
two:
	# function two load
	addi $sp, $sp, -4
	sw $ra, 0($sp)
	addi $sp, $sp, -4
	sw $s0, 0($sp)
	# function two begin
	li $s0, 1
	move $v0, $s0
	# function two unload
	lw $s0, 0($sp)
	addi $sp, $sp, 4
	lw $ra, 0($sp)
	addi $sp, $sp, 4
	jr $ra
