#!/usr/bin/env perl

# Read schema definition file
# python example: ''.join(j.capitalize() if i>0 else j.lower() for (i,j) in enumerate(x.split()))

sub KABAB {$s = join('-', map {uc $_} split(/(?=[A-Z])/, $_[0]));}
sub kabab {$s = join('-', map {lc $_} split(/(?=[A-Z])/, $_[0]));}
sub snake {$s = join('_', map {lc $_} split(/(?=[A-Z])/, $_[0]));}
sub word {$s = join(' ', map {ucfirst $_} split(/(?=[A-Z])/, $_[0]));}
sub SNAKE {uc(snake($_[0]));}
sub camel {lcfirst $_[0];}
sub Camel {ucfirst $_[0];}

my %sum_types;
my %product_types;
my %scalar_types;
my $current_type = '';
my $type_name = '';
my $type_flag = 'scalar';
my $indent = "  ";
my $type_start = 0;
my %sum_parents;
while (<>) {
  chomp;
  next if /^---$/;
  next if /^...$/;
  next if /^#/;
  my $leading_spaces = () = $_ =~ /\G\s/g;
  if ( ($leading_spaces == 0) && ($current_type ne '') ) {
    $sum_types{$type_name} = $current_type if $type_flag eq 'sum';
    $product_types{$type_name} = $current_type if $type_flag eq 'product';
    $scalar_types{$type_name} = $current_type if $type_flag eq 'scalar';
    $type_flag = 'scalar';
  }
  if ($leading_spaces == 0) {
    @_ = split(':');
    $type_name = @_[0];
    $current_type = "  - name: @_[0]\n";
    $current_type .= "    word: " . word(@_[0]) . "\n";
    $current_type .= "    camel: " . camel(@_[0]) . "\n";
    $current_type .= "    Camel: " . Camel(@_[0]) . "\n";
    $current_type .= "    KABAB: " . KABAB(@_[0]) . "\n";
    $current_type .= "    kabab: " . kabab(@_[0]) . "\n";
    $current_type .= "    snake: " . snake(@_[0]) . "\n";
    $current_type .= "    SNAKE: " . SNAKE(@_[0]) . "\n";
    $type_start = 1;
  } else {
    if ( $type_start == 1 ) {
      if (/\s+\|/) {
        $type_flag = 'sum';
        $current_type .= "    enumerations:\n";
      } else {
        $type_flag = 'product';
        $current_type .= "    parameters:\n";
      }
    }
    my $t = $_;
    $t =~ s/^\s*\|{0,1}\s*//;
    @t = split(':', $t);
    $current_type .= "      - name: $t[0]\n";
    $current_type .= "        word: " . word($t[0]) . "\n";
    $current_type .= "        camel: " . camel($t[0]) . "\n";
    $current_type .= "        Camel: " . Camel($t[0]) . "\n";
    $current_type .= "        KABAB: " . KABAB($t[0]) . "\n";
    $current_type .= "        kabab: " . kabab($t[0]) . "\n";
    $current_type .= "        snake: " . snake($t[0]) . "\n";
    $current_type .= "        SNAKE: " . SNAKE($t[0]) . "\n";
    if ( $type_flag eq 'sum' ) {
      $sum_parents{$t[0]} = $type_name;
    }
    if ( $#t > 0 ) {
      $t[1] =~ s/^\s*//;
      if ( $t[1] =~ /!/ ) {
        $t[1] =~ s/!//;
        $current_type .= "        required: true\n";
        $current_type .= "        scala_type: $t[1]\n";
      } else {
        $current_type .= "        required: false\n";
        $current_type .= "        scala_type: Option[$t[1]]\n";
      }
      $current_type .= "        type: $t[1]\n";
    }
    $type_start = 0;
  }
}
if ($current_type ne '') {
  $sum_types{$type_name} = $current_type if $type_flag eq 'sum';
  $product_types{$type_name} = $current_type if $type_flag eq 'product';
  $scalar_types{$type_name} = $current_type if $type_flag eq 'scalar';
}
for my $t (keys %sum_parents) {
  if ( exists $product_types{$t} ) {
    my @tmp = split(/\n/, $product_types{$t});
    my $tmp = shift(@tmp);
    my $rest = join("\n", @tmp);
    $product_types{$t} = $tmp . "\n" . "    parent_sum_type: " . $sum_parents{$t} . "\n" . $rest . "\n";
  } else {
    $scalar_types{$t} = "  - name: " . $t . "\n" . "    parent_sum_type: " . $sum_parents{$t} . "\n";
  }
}
print "---\n";
print "sum_types:\n";
for my $t (keys %sum_types) {
  print "$sum_types{$t}";
}
print "product_types:\n";
for my $t (keys %product_types) {
  print "$product_types{$t}";
}
print "scalar_types:\n";
for my $t (keys %scalar_types) {
  print "$scalar_types{$t}";
}