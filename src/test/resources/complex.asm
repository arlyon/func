	.data
sinp: .asciiz "enter > "
	.text
main:
	addi $sp, $sp, -4
	sw $a0, 0($sp)
	addi $sp, $sp, -4
	sw $v0, 0($sp)
	li $v0, 4
	la $a0, sinp
	syscall
	li $v0, 5syscall
	move $s0, $v0
	lw $v0, 0($sp)
	addi $sp, $sp, 4
	lw $a0, 0($sp)
	addi $sp, $sp, 4
	move $a0, $s0
	jal Sum
	move $s1, $v0
	addi $sp, $sp, -4
	sw $a0, 0($sp)
	addi $sp, $sp, -4
	sw $v0, 0($sp)
	move $a0, $s1
	li  $v0, 1
	syscall
	lw $v0, 0($sp)
	addi $sp, $sp, 4
	lw $a0, 0($sp)
	addi $sp, $sp, 4
	li $v0, 10
	syscall
sum:
	li $v0, 0
WLOOP0:
	li $t8, 0
	addi $sp, $sp, -4
	sw $t8, 0($sp)
	move $t9, $a0
	lw $t8, 0($sp)
	addi $sp, $sp, 4
	bge $t8, $t9, WEND0
	move $t8, $v0
	addi $sp, $sp, -4
	sw $t8, 0($sp)
	move $t9, $a0
	lw $t8, 0($sp)
	addi $sp, $sp, 4
	add $v0, $t8, $t9
	move $t8, $a0
	addi $sp, $sp, -4
	sw $t8, 0($sp)
	li $t9, 1
	lw $t8, 0($sp)
	addi $sp, $sp, 4
	sub $a0, $t8, $t9
	j WLOOP0
WEND0:
	jr $ra